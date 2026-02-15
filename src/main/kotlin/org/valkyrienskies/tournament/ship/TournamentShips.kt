package org.valkyrienskies.tournament.ship

import com.google.common.util.concurrent.AtomicDouble
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import org.joml.Vector3d
import org.joml.Vector3i
import org.joml.primitives.AABBd
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.api.world.PhysLevel
import org.valkyrienskies.core.api.world.properties.DimensionId
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.settings
import org.valkyrienskies.mod.common.util.toBlockPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.TickScheduler
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.blockentity.PropellerBlockEntity
import org.valkyrienskies.tournament.blocks.BallastBlock
import org.valkyrienskies.tournament.util.extension.toBlock
import org.valkyrienskies.tournament.util.extension.toDimensionKey
import org.valkyrienskies.tournament.util.extension.toDouble
import org.valkyrienskies.tournament.util.helper.Helper3d
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ConcurrentHashMap

class TournamentShips: ShipPhysicsListener {

    var level: DimensionId = "minecraft:overworld"

    data class ThrusterData(
        val pos: Vector3i,
        val force: Vector3d,
        val mult: Double,
        var submerged: Boolean
    ) {
        
        val id: String = "${pos.x}_${pos.y}_${pos.z}_${String.format("%.3f", force.x)}_${String.format("%.3f", force.y)}_${String.format("%.3f", force.z)}".replace("-", "n").replace(".", "_")
    }

    
    val thrusters =
        CopyOnWriteArrayList<ThrusterData>()

    
    private val balloons =
        CopyOnWriteArrayList<Pair<Vector3i, Double>>()

    
    data class SpinnerData(
        val pos: Vector3i,
        val torque: Vector3d
    ) {
        
        val id: String = "${pos.x}_${pos.y}_${pos.z}_${String.format("%.3f", torque.x)}_${String.format("%.3f", torque.y)}_${String.format("%.3f", torque.z)}".replace("-", "n").replace(".", "_")
    }
    
    private val spinners =
        CopyOnWriteArrayList<SpinnerData>()

    
    private val pulses =
        CopyOnWriteArrayList<Pair<Vector3d, Vector3d>>()

    data class PropellerData(
        val pos: Vector3i,
        val force: Vector3d,
        var speed: AtomicDouble,
        var touchingWater: Boolean
    )

    
    private val propellers =
        CopyOnWriteArrayList<PropellerData>()
        
    
    private val ballastSignalStrengths = mutableMapOf<Vector3i, Int>()
        
    
    private val dynamicMassChanges = mutableMapOf<Vector3i, Double>()

    
    private val thrusterAddQueue = ConcurrentHashMap.newKeySet<ThrusterData>()
    private val thrusterRemoveQueue = ConcurrentHashMap.newKeySet<String>() 
    
    
    private val spinnerAddQueue = ConcurrentHashMap.newKeySet<SpinnerData>()
    private val spinnerRemoveQueue = ConcurrentHashMap.newKeySet<String>() 

    
    private val ballastAddQueue = ConcurrentHashMap.newKeySet<Pair<BlockPos, Double>>() 
    private val ballastRemoveQueue = ConcurrentHashMap.newKeySet<BlockPos>()

    
    private val ballastPositions = mutableSetOf<BlockPos>()

    
    private var ticker: TickScheduler.Ticking? = null

    override fun physTick(physShip: PhysShip, physLevel: PhysLevel) {
        physShip as PhysShipImpl

        if (ticker == null) {
            ticker = TickScheduler.serverTickPerm { server ->
                val lvl = server.getLevel(level.toDimensionKey())
                    ?: return@serverTickPerm

                
                thrusters.forEach { t ->
                    val water = lvl.isWaterAt(
                        Helper3d
                            .convertShipToWorldSpace(lvl, t.pos.toDouble())
                            .toBlock()
                    )
                    t.submerged = water
                }

                
                propellers.forEach { p ->
                    
                    val water = lvl.isWaterAt(
                        Helper3d
                            .convertShipToWorldSpace(lvl, p.pos.toDouble())
                            .toBlock()
                    )
                    p.touchingWater = water

                    val be = lvl.getBlockEntity(
                        p.pos.toBlockPos()
                    ) as PropellerBlockEntity<*>?

                    if (be != null) {
                        p.speed.set(be.speed)
                    }
                }
                
                
                checkBallastRedstoneSignals(lvl)
            }
        }

        
        processQueues()

        val vel = physShip.velocity

        
        thrusters.forEach { data ->
            val (pos, force, mult, submerged) = data

            if (submerged) {
                return@forEach
            }

            val tForce = physShip.transform.shipToWorld.transformDirection(force, Vector3d())
            val tPos = pos.toDouble().add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)

            if (force.isFinite && (
                TournamentConfig.SERVER.thrusterShutoffSpeed == -1.0
                    || physShip.velocity.length() < TournamentConfig.SERVER.thrusterShutoffSpeed
                )
            ) {
                
                
                physShip.applyInvariantForceToPos(tForce.mul(TournamentConfig.SERVER.thrusterSpeed * mult), tPos)
            }
        }

        
        balloons.forEach {
            val (pos, pow) = it

            val tPos = Vector3d(pos).add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)
            val tHeight = physShip.transform.positionInWorld.y()
            var tPValue = TournamentConfig.SERVER.balloonBaseHeight - ((tHeight * tHeight) / 1000.0)

            if (vel.y() > 10.0)    {
                tPValue = (-vel.y() * 0.25)
                tPValue -= (vel.y() * 0.25)
            }
            if(tPValue <= 0){
                tPValue = 0.0
            }
            physShip.applyInvariantForceToPos(
                Vector3d(
                    0.0,
                    pow * TournamentConfig.SERVER.balloonPower * tPValue,  
                    0.0
                ),
                tPos
            )
        }

        
        spinners.forEach { spinnerData ->
            val torque = spinnerData.torque

            val torqueGlobal = physShip.transform.shipToWorldRotation.transform(torque, Vector3d())

            physShip.applyInvariantTorque(torqueGlobal.mul(TournamentConfig.SERVER.spinnerSpeed))
        }

        
        
        
        for ((pos, mass) in dynamicMassChanges) {
            
            
        }

        
        pulses.forEach {
            val (pos, force) = it
            val tPos = pos.add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)
            val tForce = physShip.transform.worldToShip.transformDirection(force)

            physShip.applyRotDependentForceToPos(tForce, tPos)
        }
        pulses.clear()

        
        propellers.forEach {
            val (pos, force, speed, touchingWater) = it

            if (!touchingWater) {
                return@forEach
            }

            val tPos = pos.toDouble().add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)
            val tForce = physShip.transform.shipToWorld.transformDirection(force, Vector3d())

            physShip.applyInvariantForceToPos(tForce.mul(speed.get()), tPos)
        }
    }

    
    private fun checkBallastRedstoneSignals(level: Level) {
        val positionsToRemove = mutableListOf<BlockPos>()
        
        for (pos in ballastPositions) {
            
            val blockState = level.getBlockState(pos)
            if (blockState.block is BallastBlock) {
                val currentPower = blockState.getValue(BallastBlock.POWER)
                val previousPower = ballastSignalStrengths[pos.toJOML()]
                
                
                if (previousPower != currentPower) {
                    
                    val newMass = calculateMass(currentPower)
                    
                    
                    updateBallastMass(pos, newMass)
                    
                    
                    ballastSignalStrengths[pos.toJOML()] = currentPower
                }
            } else {
                
                positionsToRemove.add(pos)
            }
        }
        
        
        positionsToRemove.forEach { pos ->
            ballastPositions.remove(pos)
            removeBallast(pos)
            ballastSignalStrengths.remove(pos.toJOML())
        }
    }

    
    private fun calculateMass(power: Int): Double {
        val normalizedPower = power.coerceIn(0, 15) / 15.0 
        return TournamentConfig.SERVER.ballastWeight + 
               (TournamentConfig.SERVER.ballastNoWeight - TournamentConfig.SERVER.ballastWeight) * normalizedPower
    }
    
    
    private fun updateBallastMass(pos: BlockPos, newMass: Double) {
        
        ballastRemoveQueue.add(pos)
        
        ballastAddQueue.add(Pair(pos, newMass))
    }

    
    private fun processQueues() {
        
        thrusterAddQueue.forEach { thrusterData ->
            
            val existingIndex = thrusters.indexOfFirst { it.id == thrusterData.id }
            if (existingIndex == -1) {
                thrusters.add(thrusterData)
            } else {
                
                thrusters[existingIndex] = thrusterData
            }
        }
        thrusterAddQueue.clear()

        
        thrusterRemoveQueue.forEach { id ->
            thrusters.removeIf { it.id == id }
        }
        thrusterRemoveQueue.clear()

        
        spinnerAddQueue.forEach { spinnerData ->
            
            val existingIndex = spinners.indexOfFirst { it.id == spinnerData.id }
            if (existingIndex == -1) {
                spinners.add(spinnerData)
            } else {
                
                spinners[existingIndex] = spinnerData
            }
        }
        spinnerAddQueue.clear()

        
        spinnerRemoveQueue.forEach { id ->
            spinners.removeIf { it.id == id }
        }
        spinnerRemoveQueue.clear()

        
        ballastAddQueue.forEach { (pos, weight) ->
            val jomlPos = pos.toJOML()
            
            dynamicMassChanges[jomlPos] = weight
        }
        ballastAddQueue.clear()

        
        ballastRemoveQueue.forEach { pos ->
            val jomlPos = pos.toJOML()
            dynamicMassChanges.remove(jomlPos)
        }
        ballastRemoveQueue.clear()
    }

    
    fun addThruster(
        pos: BlockPos,
        mult: Double,  
        force: Vector3d
    ) {
        
        val thrusterData = ThrusterData(pos.toJOML(), force, mult, false)
        thrusterAddQueue.add(thrusterData)
    }

    
    fun stopThruster(
        pos: BlockPos
    ) {
        
        
        thrusters.filter { it.pos == pos.toJOML() }.forEach { thruster ->
            thrusterRemoveQueue.add(thruster.id)
        }
    }
    
    
    fun stopThruster(
        pos: BlockPos,
        force: Vector3d
    ) {
        val id = "${pos.x}_${pos.y}_${pos.z}_${String.format("%.3f", force.x)}_${String.format("%.3f", force.y)}_${String.format("%.3f", force.z)}".replace("-", "n").replace(".", "_")
        thrusterRemoveQueue.add(id)
    }

    
    fun addBalloon(pos: BlockPos, pow: Double) {
        balloons.add(pos.toJOML() to pow)
    }

    
    fun addBalloons(list: Iterable<Pair<Vector3i, Double>>) {
        balloons.addAll(list)
    }

    
    fun removeBalloon(pos: BlockPos) {
        balloons.removeAll { it.first == pos.toJOML() }
    }

    
    fun addSpinner(pos: Vector3i, torque: Vector3d) {
        
        val spinnerData = SpinnerData(pos, torque)
        spinnerAddQueue.add(spinnerData)
    }

    
    fun addSpinners(list: Iterable<Pair<Vector3i, Vector3d>>) {
        list.forEach { (pos, torque) ->
            val spinnerData = SpinnerData(pos, torque)
            spinnerAddQueue.add(spinnerData)
        }
    }

    
    fun removeSpinner(pos: Vector3i) {
        
        spinners.filter { it.pos == pos }.forEach { spinner ->
            spinnerRemoveQueue.add(spinner.id)
        }
    }
    
    
    fun removeSpinner(pos: Vector3i, torque: Vector3d) {
        val id = "${pos.x}_${pos.y}_${pos.z}_${String.format("%.3f", torque.x)}_${String.format("%.3f", torque.y)}_${String.format("%.3f", torque.z)}".replace("-", "n").replace(".", "_")
        spinnerRemoveQueue.add(id)
    }

    
    fun addPulse(pos: Vector3d, force: Vector3d) {
        pulses.add(pos to force)
    }

    
    fun addPulses(list: Iterable<Pair<Vector3d, Vector3d>>) {
        pulses.addAll(list)
    }

    
    fun addPropeller(pos: Vector3i, force: Vector3d) {
        propellers += PropellerData(pos, force, AtomicDouble(), false)
    }

    
    fun removePropeller(pos: Vector3i) {
        propellers.removeIf { it.pos == pos }
    }
    
    
    fun addHeavyBallast(pos: BlockPos, weight: Double) {
        val jomlPos = pos.toJOML()
        
        removeBallast(pos)
        
        ballastAddQueue.add(Pair(pos, weight))
        
        ballastPositions.add(pos)
        
        val blockState = pos.let { 
            
            
            0 
        }
    }
    
    
    fun addLightBallast(pos: BlockPos, weight: Double) {
        addHeavyBallast(pos, weight)
    }
    
    
    fun removeBallast(pos: BlockPos) {
        val jomlPos = pos.toJOML()
        
        ballastRemoveQueue.add(pos)
        
        ballastPositions.remove(pos)
        ballastSignalStrengths.remove(jomlPos)
    }

    companion object {
        fun getOrCreate(ship: LoadedShip, level: DimensionId): TournamentShips {
            if (ship is LoadedServerShip) {
                return ship.getAttachment(TournamentShips::class.java)
                    ?: TournamentShips().also {
                        it.level = level
                        ship.setAttachment(it)
                        
                        it.scanAndAddAllBalloons(ship, level)
                    }
            } else {
                
                return TournamentShips().also {
                    it.level = level
                }
            }
        }

        fun getOrCreate(ship: LoadedShip): TournamentShips =
            getOrCreate(ship, ship.chunkClaimDimension)
    }
    
    
    private fun scanAndAddAllBalloons(serverShip: LoadedServerShip, level: DimensionId) {
        val server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() ?: return
        val world = server.getLevel(level.toDimensionKey()) ?: return
        
        val bounds = serverShip.shipAABB ?: return
        
        
        for (x in bounds.minX().toInt()..bounds.maxX().toInt()) {
            for (y in bounds.minY().toInt()..bounds.maxY().toInt()) {
                for (z in bounds.minZ().toInt()..bounds.maxZ().toInt()) {
                    val blockPos = net.minecraft.core.BlockPos(x, y, z)
                    val blockState = world.getBlockState(blockPos)
                    
                    
                    if (blockState.block == org.valkyrienskies.tournament.TournamentBlocks.BALLOON.get()) {
                        
                        addBalloon(blockPos, org.valkyrienskies.tournament.TournamentConfig.SERVER.balloonPower)
                    } else if (blockState.block == org.valkyrienskies.tournament.TournamentBlocks.POWERED_BALLOON.get()) {
                        
                        val signal = world.getBestNeighborSignal(blockPos)
                        if (signal > 0) {
                            val power = signal.toDouble() * org.valkyrienskies.tournament.TournamentConfig.SERVER.poweredBalloonAnalogStrength
                            addBalloon(blockPos, power)
                        }
                    }
                }
            }
        }
    }
}

private fun <T> MutableList<T>.addIfNotPresent(element: T): Boolean {
    if (this.contains(element)) {
        return false
    }
    return this.add(element)
}