/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.IRSensor;

public class ManualIndexer extends CommandBase {
  /**
   * Creates a new ManualIndexer.
   */
  double speed;
  
  public static IRSensor subsystem;
  public ManualIndexer(IRSensor sub, double pSpeed) {
    speed = pSpeed;
    subsystem = sub;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (speed < 0) {
      RobotContainer.ShooterMotor.set(speed*.5);
    } else {
      RobotContainer.ShooterMotor.set(speed);

    }
    RobotContainer.IndexerMotor.set(speed);

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    RobotContainer.ShooterMotor.set(0);
    RobotContainer.IndexerMotor.set(0);

  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
