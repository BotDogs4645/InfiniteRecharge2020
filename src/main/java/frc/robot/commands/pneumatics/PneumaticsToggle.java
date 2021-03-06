/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.pneumatics;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.PneumaticsSubsystem;

public class PneumaticsToggle extends CommandBase {
  
  //Subsystem the command runs on
  public static PneumaticsSubsystem subsystem;
    public PneumaticsToggle(PneumaticsSubsystem sub)
    {
        subsystem = sub;
        addRequirements(subsystem); 
    }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    subsystem.toggle();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    //Set the motor speed back to 0% (stop) when command is finished
    
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    
    return false;
  }
}
