package org.valkyrienskies.tournament

import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema

object TournamentConfig {
    @JvmField
    val CLIENT = Client()

    @JvmField
    val SERVER = Server()

    class Client {

    }

    class Server {
        @JsonSchema(description = "The force a spinner applies to a ship")
        var spinnerSpeed = 5000.0

        @JsonSchema(description = "The force a balloon applies to a ship")
        var balloonPower = 30.0

        @JsonSchema(description = "How much stronger a powered balloon will get when powered (1.0 is 15x stronger at max power)")
        var poweredBalloonAnalogStrength = 2.0

        @JsonSchema(description = "Base height of a balloon")
        var balloonBaseHeight = 100.0

        @JsonSchema(description = "The force a thruster applies to a ship * tier")
        var thrusterSpeed = 10000.0

        @JsonSchema(description = "The maximum amount of tiers a normal thruster can have (1-5)")
        var thrusterTiersNormal = 5

        @JsonSchema(description = "The maximum amount of tiers a tiny thruster can have (1-5)")
        var thrusterTiersTiny = 1

        @JsonSchema(description = "The force multiplier of a tiny thruster")
        var thrusterTinyForceMultiplier = 0.2

        @JsonSchema(description = "The speed at which the thruster will stop applying force. (-1 means that it always applies force)")
        var thrusterShutoffSpeed = -1.0

        @JsonSchema(description = "Maximum speed of big propeller")
        var propellerBigSpeed = 10.0

        @JsonSchema(description = "Acceleration rate of big propeller")
        var propellerBigAccel = 0.5

        @JsonSchema(description = "Maximum speed of small propeller")
        var propellerSmallSpeed = 5.0

        @JsonSchema(description = "Acceleration rate of small propeller")
        var propellerSmallAccel = 0.2

        @JsonSchema(description = "The weight of a ballast when not redstone powered")
        var ballastWeight = 10000.0

        @JsonSchema(description = "The weight of a ballast when redstone powered")
        var ballastNoWeight = 800.0

        @JsonSchema(description = "The force the pulse gun applies to a ship")
        var pulseGunForce = 300.0

        @JsonSchema(description = "Maximum distance a sensor can detect a ship from")
        var sensorDistance = 10.0 

        
    }
}