package com.omagarwal25.swervelib;

public interface DriveController {
    void setReferenceVoltage(double voltage);

    double getStateVelocity();
}
