/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.TankDrive;

public class Align extends CommandBase {
    public static Limelight limelightsubsytem;
    public static TankDrive tankdrivesubsytem;

  public Align(Limelight sub,TankDrive subtwo) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    limelightsubsytem = sub;
    tankdrivesubsytem = subtwo;
    addRequirements(limelightsubsytem); 
    addRequirements(tankdrivesubsytem); 
    
  }

  // Called just before this Command runs the first time
  @Override
  public void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  public void execute() {

  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  public boolean isFinished() { //write comments!
    
    double offset = limelightsubsytem.getXOffset();
    if (offset < -1.5){

      tankdrivesubsytem.turn(false);
    } else if(offset > 1.5){

      tankdrivesubsytem.turn(true);
    } else {
      //Robot.tankDrive.stop();
      return true;
    }
    
    
    return false;
  }

  // Called once after isFinished returns true
  protected void end() {
    tankdrivesubsytem.stop();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  protected void interrupted() {
  }
}
