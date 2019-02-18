/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.ControlMode;
import frc.robot.RobotMap;
import frc.robot.commands.backStiltSpeedCommand;

/**
 * armSubsystem controls the cargo collection arm's up and down movement.
 * 
 * Reference Code:
 *   https://github.com/CrossTheRoadElec/Phoenix-Examples-Languages/blob/master/Java/PositionClosedLoop/src/main/java/frc/robot/Robot.java
 *   https://github.com/Spartronics4915/developers_handbook/blob/master/actuators/talon/programming.md#setting-follower-mode
 */
public class backStiltSubsystem extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  public TalonSRX backStrut = new TalonSRX(RobotMap.BACK_STRUT);
  StringBuilder _sb = new StringBuilder();
  private int startPosition = 0;
  private int targetPosition = 0;
  private int kPIDLoopIdx = 0;
  private int kTimeoutMs = 3;  // 30
  public double kP;  // 0.15
  public double kI;
  public double kD;  // 1.0
  public double kF;
  private int allowableError = 200;   // allowable error in encoder ticks

  @Override
  public void initDefaultCommand() {
    // default to manual control
    setDefaultCommand(new backStiltSpeedCommand());
  }

  public void init() {
    backStrut.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, kPIDLoopIdx,
        kTimeoutMs);
    /* Ensure sensor is positive when output is positive */
    backStrut.setSensorPhase(false);
    backStrut.setInverted(false);
    /* Config the peak and nominal outputs, 12V means full */
    backStrut.configNominalOutputForward(0, kTimeoutMs);
    backStrut.configNominalOutputReverse(0, kTimeoutMs);
    backStrut.configPeakOutputForward(1, kTimeoutMs);
    backStrut.configPeakOutputReverse(-1, kTimeoutMs);
    backStrut.configAllowableClosedloopError(allowableError, kPIDLoopIdx, kTimeoutMs);
    /* Config Position Closed Loop gains in slot0, typically kF stays zero. */
    backStrut.config_kF(kPIDLoopIdx, kF, kTimeoutMs);
    backStrut.config_kP(kPIDLoopIdx, kP, kTimeoutMs);
    backStrut.config_kI(kPIDLoopIdx, kI, kTimeoutMs);
    backStrut.config_kD(kPIDLoopIdx, kD, kTimeoutMs);
    /**
     * Grab the 360 degree position of the MagEncoder's absolute position, and initially set the
     * relative sensor to match.
     */
    startPosition = backStrut.getSensorCollection().getPulseWidthPosition();

    _sb.append("BACKSTRUT: start position ");
    _sb.append(startPosition);
    System.out.println(_sb);
    _sb.setLength(0);

    /* Mask out overflows, keep bottom 12 bits. Value will be 0-4096 */
    //startPosition &= 0xFFF;
    backStrut.setSelectedSensorPosition(startPosition, kPIDLoopIdx, kTimeoutMs);

    targetPosition = startPosition - 0;

    printDebug("INIT");
  }

  /*
  * setPosition()
  * 
  * Tell the arm motor to move to the given targetPosition. Target position is relative
  * to the start position when the robot turns on. Position is measured in encoder ticks.
  */
  public void setPosition(int desiredPosition) {
    targetPosition = startPosition - desiredPosition;
    backStrut.config_kF(kPIDLoopIdx, kF, kTimeoutMs);
    backStrut.config_kP(kPIDLoopIdx, kP, kTimeoutMs);
    backStrut.config_kI(kPIDLoopIdx, kI, kTimeoutMs);
    backStrut.config_kD(kPIDLoopIdx, kD, kTimeoutMs);
    backStrut.configAllowableClosedloopError(allowableError, kPIDLoopIdx, kTimeoutMs);
    backStrut.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, kPIDLoopIdx,
    kTimeoutMs);
    backStrut.setIntegralAccumulator(0.0);  // zero out the kI error accumulator
    backStrut.set(ControlMode.Position, targetPosition);
  }

  public int getPosition() {
    return backStrut.getSensorCollection().getPulseWidthPosition();
  }

  public int getStartPosition() { return startPosition; }

  public int getTargetPosition() { return targetPosition; }

  public boolean isAtTargetPosition(int desiredPosition) {
    if (Math.abs(getPosition() - (startPosition - desiredPosition)) <= allowableError) {
        return true;
    }
    else {
        return false;
    }
  }
   
   // Set the back stilt climb motor speed, input from [-1,1]
  public void setBackClimberSpeed(double speed) {
    backStrut.set(ControlMode.PercentOutput, speed);
    printDebug("speed");
  }

  // debug the encoder positions and motor output for PID
  public void printDebug(String name) {
    _sb.append("BACKSTRUT out:");
    double motorOutput = backStrut.getMotorOutputPercent();
    _sb.append((int) (motorOutput * 100));
    _sb.append("%"); // Percent
    _sb.append("\tpos:");
    _sb.append(backStrut.getSelectedSensorPosition(0));
    _sb.append("u"); // Native units
    _sb.append("\ttarget:");
    _sb.append(targetPosition);
    _sb.append("u"); // Native Units
    _sb.append("\tstart:");
    _sb.append(startPosition);
    _sb.append("u"); // Native Units
    _sb.append("\terr:");
    _sb.append(backStrut.getClosedLoopError(0));
    _sb.append("u");	// Native Units
    _sb.append("\tspd:");
		_sb.append(backStrut.getSelectedSensorVelocity(0));
		_sb.append("u");
    _sb.append(" " + name);
    System.out.println(_sb);
    /* Reset built string for next loop */
    _sb.setLength(0);
  }
}
