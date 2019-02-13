/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;


public class armMiddleCommand extends armCommand {

  public armMiddleCommand() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);

    // targetPossition, kP, kI, kP, kF
    super(800, 0.3, 0.0, 0.0, 0.0);

    name = "Middle";
  }
}
