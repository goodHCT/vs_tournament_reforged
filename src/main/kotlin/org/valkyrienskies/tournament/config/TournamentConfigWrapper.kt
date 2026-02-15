package org.valkyrienskies.tournament.config

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig
import org.valkyrienskies.tournament.TournamentConfig


object TournamentConfigWrapper {
    private val COMMON_CONFIG_SPEC: ForgeConfigSpec
    private val CLIENT_CONFIG_SPEC: ForgeConfigSpec

    
    lateinit var SPINNER_SPEED: ForgeConfigSpec.DoubleValue
    lateinit var BALLOON_POWER: ForgeConfigSpec.DoubleValue
    lateinit var POWERED_BALLOON_ANALOG_STRENGTH: ForgeConfigSpec.DoubleValue
    lateinit var BALLOON_BASE_HEIGHT: ForgeConfigSpec.DoubleValue
    lateinit var THRUSTER_SPEED: ForgeConfigSpec.DoubleValue
    lateinit var THRUSTER_TIERS_NORMAL: ForgeConfigSpec.IntValue
    lateinit var THRUSTER_TIERS_TINY: ForgeConfigSpec.IntValue
    lateinit var THRUSTER_TINY_FORCE_MULTIPLIER: ForgeConfigSpec.DoubleValue
    lateinit var THRUSTER_SHUTOFF_SPEED: ForgeConfigSpec.DoubleValue
    lateinit var BALLAST_WEIGHT: ForgeConfigSpec.DoubleValue
    lateinit var BALLAST_NO_WEIGHT: ForgeConfigSpec.DoubleValue
    lateinit var PULSE_GUN_FORCE: ForgeConfigSpec.DoubleValue
    lateinit var SENSOR_DISTANCE: ForgeConfigSpec.DoubleValue
    lateinit var PROPELLER_BIG_SPEED: ForgeConfigSpec.DoubleValue
    lateinit var PROPELLER_BIG_ACCEL: ForgeConfigSpec.DoubleValue
    lateinit var PROPELLER_SMALL_SPEED: ForgeConfigSpec.DoubleValue
    lateinit var PROPELLER_SMALL_ACCEL: ForgeConfigSpec.DoubleValue

    init {
        val commonBuilder = ForgeConfigSpec.Builder()
        setupCommonConfig(commonBuilder)
        COMMON_CONFIG_SPEC = commonBuilder.build()

        val clientBuilder = ForgeConfigSpec.Builder()
        setupClientConfig(clientBuilder)
        CLIENT_CONFIG_SPEC = clientBuilder.build()
    }

    private fun setupCommonConfig(builder: ForgeConfigSpec.Builder) {
        builder.push("server")
        
        SPINNER_SPEED = builder
            .comment("The force a spinner applies to a ship")
            .defineInRange("spinnerSpeed", 5000.0, 0.0, Double.MAX_VALUE)
        
        BALLOON_POWER = builder
            .comment("The force a balloon applies to a ship")
            .defineInRange("balloonPower", 30.0, 0.0, Double.MAX_VALUE)
        
        POWERED_BALLOON_ANALOG_STRENGTH = builder
            .comment("How much stronger a powered balloon will get when powered (1.0 is 15x stronger at max power)")
            .defineInRange("poweredBalloonAnalogStrength", 2.0, 0.0, Double.MAX_VALUE)
        
        BALLOON_BASE_HEIGHT = builder
            .comment("Base height of a balloon")
            .defineInRange("balloonBaseHeight", 100.0, 0.0, Double.MAX_VALUE)
        
        THRUSTER_SPEED = builder
            .comment("The force a thruster applies to a ship * tier")
            .defineInRange("thrusterSpeed", 10000.0, 0.0, Double.MAX_VALUE)
        
        THRUSTER_TIERS_NORMAL = builder
            .comment("The maximum amount of tiers a normal thruster can have (1-5)")
            .defineInRange("thrusterTiersNormal", 5, 1, 5)  
        
        THRUSTER_TIERS_TINY = builder
            .comment("The maximum amount of tiers a tiny thruster can have (1-5)")
            .defineInRange("thrusterTiersTiny", 1, 1, 1)  
        
        THRUSTER_TINY_FORCE_MULTIPLIER = builder
            .comment("The force multiplier of a tiny thruster")
            .defineInRange("thrusterTinyForceMultiplier", 0.2, 0.0, 1.0)
        
        THRUSTER_SHUTOFF_SPEED = builder
            .comment("The speed at which the thruster will stop applying force. (-1 means that it always applies force)")
            .defineInRange("thrusterShutoffSpeed", -1.0, -1.0, Double.MAX_VALUE)
        
        BALLAST_WEIGHT = builder
            .comment("The weight of a ballast when not redstone powered")
            .defineInRange("ballastWeight", 10000.0, 0.0, Double.MAX_VALUE)
        
        BALLAST_NO_WEIGHT = builder
            .comment("The weight of a ballast when redstone powered")
            .defineInRange("ballastNoWeight", 800.0, 0.0, Double.MAX_VALUE)
        
        PULSE_GUN_FORCE = builder
            .comment("The force the pulse gun applies to a ship")
            .defineInRange("pulseGunForce", 300.0, 0.0, Double.MAX_VALUE)
        
        PROPELLER_BIG_SPEED = builder
            .comment("Maximum speed of big propeller")
            .defineInRange("propellerBigSpeed", 10.0, 0.0, Double.MAX_VALUE)
        
        PROPELLER_BIG_ACCEL = builder
            .comment("Acceleration rate of big propeller")
            .defineInRange("propellerBigAccel", 0.5, 0.0, Double.MAX_VALUE)
        
        PROPELLER_SMALL_SPEED = builder
            .comment("Maximum speed of small propeller")
            .defineInRange("propellerSmallSpeed", 5.0, 0.0, Double.MAX_VALUE)
        
        PROPELLER_SMALL_ACCEL = builder
            .comment("Acceleration rate of small propeller")
            .defineInRange("propellerSmallAccel", 0.2, 0.0, Double.MAX_VALUE)
        
        SENSOR_DISTANCE = builder
            .comment("Maximum distance a sensor can detect a ship from")
            .defineInRange("sensorDistance", 10.0, 0.0, Double.MAX_VALUE)
        
        builder.pop()
    }

    private fun setupClientConfig(builder: ForgeConfigSpec.Builder) {
        builder.push("client")
        builder.pop()
    }

    
    fun registerConfigs() {
        val context = ModLoadingContext.get()
        context.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG_SPEC)
        context.registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG_SPEC)
    }

    
    fun syncConfigValues() {
        
        TournamentConfig.SERVER.spinnerSpeed = SPINNER_SPEED.get()
        TournamentConfig.SERVER.balloonPower = BALLOON_POWER.get()
        TournamentConfig.SERVER.poweredBalloonAnalogStrength = POWERED_BALLOON_ANALOG_STRENGTH.get()
        TournamentConfig.SERVER.balloonBaseHeight = BALLOON_BASE_HEIGHT.get()
        TournamentConfig.SERVER.thrusterSpeed = THRUSTER_SPEED.get()
        TournamentConfig.SERVER.thrusterTiersNormal = THRUSTER_TIERS_NORMAL.get()
        TournamentConfig.SERVER.thrusterTiersTiny = THRUSTER_TIERS_TINY.get()
        TournamentConfig.SERVER.thrusterTinyForceMultiplier = THRUSTER_TINY_FORCE_MULTIPLIER.get()
        TournamentConfig.SERVER.thrusterShutoffSpeed = THRUSTER_SHUTOFF_SPEED.get()  
        TournamentConfig.SERVER.ballastWeight = BALLAST_WEIGHT.get()
        TournamentConfig.SERVER.ballastNoWeight = BALLAST_NO_WEIGHT.get()
        TournamentConfig.SERVER.pulseGunForce = PULSE_GUN_FORCE.get()
        TournamentConfig.SERVER.propellerBigSpeed = PROPELLER_BIG_SPEED.get()
        TournamentConfig.SERVER.propellerBigAccel = PROPELLER_BIG_ACCEL.get()
        TournamentConfig.SERVER.propellerSmallSpeed = PROPELLER_SMALL_SPEED.get()
        TournamentConfig.SERVER.propellerSmallAccel = PROPELLER_SMALL_ACCEL.get()
        
        TournamentConfig.SERVER.sensorDistance = SENSOR_DISTANCE.get()
        
        
        
        
    }
}