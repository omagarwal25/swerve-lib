package com.omagarwal25.swervelib.ctre;

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.omagarwal25.swervelib.AbsoluteEncoder;
import com.omagarwal25.swervelib.AbsoluteEncoderFactory;

public class CanCoderFactoryBuilder {
    // private Direction direction = Direction.COUNTER_CLOCKWISE;
    private int periodMilliseconds = 10;

    public CanCoderFactoryBuilder withReadingUpdatePeriod(int periodMilliseconds) {
        this.periodMilliseconds = periodMilliseconds;
        return this;
    }

    public CanCoderFactoryBuilder withDirection(Direction direction) {
        // this.direction = direction;
        return this;
    }

    public AbsoluteEncoderFactory<CanCoderAbsoluteConfiguration> build() {
        return configuration -> {
            TalonFXConfiguration config = new TalonFXConfiguration();
            config.absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
            // config.magnetOffsetDegrees = Math.toDegrees(configuration.getOffset());
            // config.sensorDirection = direction == Direction.CLOCKWISE;

            TalonFX encoder = new TalonFX(configuration.getId());
            CtreUtils.checkCtreError(encoder.configAllSettings(config, 250), "Failed to configure CANCoder");

            CtreUtils.checkCtreError(
                    encoder.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, periodMilliseconds, 250),
                    "Failed to configure Talon update rate");

            return new EncoderImplementation(encoder);
        };
    }

    private static class EncoderImplementation implements AbsoluteEncoder {
        private final TalonFX encoder;

        private EncoderImplementation(TalonFX encoder) {
            this.encoder = encoder;
        }

        @Override
        public double getAbsoluteAngle() {
            // check this if this works

            double angle = (encoder.getSelectedSensorPosition() % (2048.0*12.8));
            angle *= ((2.0*Math.PI)/2048.0*12.8);
//
//                    Math.toRadians(encoder.getSelectedSensorPosition()) / 12.8;
//            angle %= 2.0 * Math.PI;
            if (angle < 0.0) {
                angle += 2.0 * Math.PI;
            }

            return angle;
        }
    }

    public enum Direction {
        CLOCKWISE,
        COUNTER_CLOCKWISE
    }
}
