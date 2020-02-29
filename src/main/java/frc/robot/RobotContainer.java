/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.commands.Align;
import frc.robot.commands.ExampleCommand;
import frc.robot.commands.ManualIndexer;
import frc.robot.commands.ManualIntake;
import frc.robot.commands.RunIntake;
import frc.robot.subsystems.GearShiftSubsystem;
import frc.robot.subsystems.IRSensor;
import frc.robot.subsystems.IntakeMotor;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.PneumaticsSubsystem;
import frc.robot.subsystems.PressureSensor;
import frc.robot.subsystems.Shooter;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.*;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj.util.Units;
import frc.robot.subsystems.TankDrive;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import java.util.List;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import frc.robot.commands.RunShooter;
import frc.robot.commands.ToggleLimelight;
import frc.robot.commands.gearshift.GearShiftCommand;
import frc.robot.commands.pneumatics.PneumaticsToggle;



/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final ExampleCommand m_autoCommand = new ExampleCommand();

  UsbCamera camera1 = CameraServer.getInstance().startAutomaticCapture(0);

  private static final IntakeMotor m_intakeSub = new IntakeMotor();
  private static final Shooter shooterSub = new Shooter();

  public static final Limelight limelight = new Limelight();

  private static final IRSensor sensor = new IRSensor();

  public static final TankDrive tankDriveSubsystem = new TankDrive(); 
  public static final PneumaticsSubsystem m_pneumaticssubsytem = new PneumaticsSubsystem();
  public static final GearShiftSubsystem m_gearshiftsubsystem = new GearShiftSubsystem();
  private static final PressureSensor m_pressuresensorsubsytem = new PressureSensor();



  //TANK DRIVE MOTORS
  public static final WPI_TalonSRX frontLeft = new WPI_TalonSRX(15); //0
  public static final WPI_TalonSRX middleLeft = new WPI_TalonSRX(13); //13
  public static final WPI_TalonSRX rearLeft = new WPI_TalonSRX(11); //11

  public static final WPI_TalonSRX frontRight = new WPI_TalonSRX(12); //12
  public static final WPI_TalonSRX middleRight = new WPI_TalonSRX(14); //1
  public static final WPI_TalonSRX rearRight = new WPI_TalonSRX(10);

  public static final SpeedControllerGroup leftSide = new SpeedControllerGroup(frontLeft, middleLeft, rearLeft);
  public static final SpeedControllerGroup rightSide = new SpeedControllerGroup(frontRight, middleRight, rearRight);
  public static final DifferentialDrive difDrive = new DifferentialDrive(leftSide, rightSide);
  

  //INDEXER MOTORS
  public static final WPI_TalonSRX IntakeMotor = new WPI_TalonSRX(4);
  public static final WPI_TalonSRX IndexerMotor = new WPI_TalonSRX(5);
  public static final WPI_TalonSRX ShooterMotor= new WPI_TalonSRX(2);

  //CLIMBING PNEUMATICS
  public static final DoubleSolenoid RightPiston = new DoubleSolenoid(3,2);
  public static final DoubleSolenoid LeftPiston = new DoubleSolenoid(1,0);

  //GEARSHIFT SOLENOIDS
  public static final Solenoid gearshift1 = new Solenoid(4);
  public static final Solenoid gearshift2 = new Solenoid(5);

  //MAIN JOYSTICK
  public static final Joystick stick = new Joystick(0);

  public static JoystickButton GearShiftButton = new JoystickButton(stick,1);


  //XBOX CONTROLLER
  public static XboxController Xbox = new XboxController(1);

  public static JoystickButton indexerIntakeButton = new JoystickButton(Xbox,3);


  public static JoystickButton leftjoystickbutton = new JoystickButton(Xbox, 9);
  public static JoystickButton rightjoystickbutton = new JoystickButton(Xbox,10);

  public static JoystickButton forwardIndexerButton = new JoystickButton(Xbox,1);
  public static JoystickButton reverseIndexerButton = new JoystickButton(Xbox,2);

  public static JoystickButton shooterButton = new JoystickButton(Xbox,6);

  public static JoystickButton alignButton = new JoystickButton(stick,11);
  public static JoystickButton limelightToggleButton = new JoystickButton(stick,12);

  /*
  // Create a voltage constraint to ensure we don't accelerate too fast
  private static final DifferentialDriveVoltageConstraint autoVoltageConstraint =
    new DifferentialDriveVoltageConstraint(
      new SimpleMotorFeedforward(Constants.ksVolts,
                               Constants.kvVoltSecondsPerMeter,
                               Constants.kaVoltSecondsSquaredPerMeter),
    Constants.kDriveKinematics,
    10);

  // Create config for trajectory
  private static final TrajectoryConfig config = 
    new TrajectoryConfig(Constants.kMaxSpeedMetersPerSecond, 
                         Constants.kMaxAccelerationMetersPerSecondSquared)
          //Add kinematics to ensure max speed is actually obeyed
          .setKinematics(Constants.kDriveKinematics)
          //Apply the auto voltage constraint
          .addConstraint(autoVoltageConstraint);

  private static final Trajectory autoTrajectory = TrajectoryGenerator.generateTrajectory(
    //Start at origin facing the +X direction
    new Pose2d(0, 0, new Rotation2d(0)),
    //Wavepoints making a S curve path
    List.of(
      new Translation2d(Units.inchesToMeters(12), 0),
      new Translation2d(Units.inchesToMeters(36), 0),
      new Translation2d(Units.inchesToMeters(60), 0),
      new Translation2d(Units.inchesToMeters(113), Units.inchesToMeters(68)),
      new Translation2d(Units.inchesToMeters(142), Units.inchesToMeters(68)),
      new Translation2d(Units.inchesToMeters(176), Units.inchesToMeters(68)),
      new Translation2d(Units.inchesToMeters(207), Units.inchesToMeters(68)),
      new Translation2d(Units.inchesToMeters(60), 0)
      ),
    //End 3 feet ahead of where robot start, facing forward
    new Pose2d(Units.feetToMeters(3), 0, new Rotation2d(0)),
    //Pass config
    config
    );

  private static final Trajectory autoTrajectory2 = TrajectoryGenerator.generateTrajectory(
    //Start at origin facing the +X direction
    new Pose2d(0, 0, new Rotation2d(0)),
    //Wavepoints making a S curve path
    List.of(
      new Translation2d(Units.feetToMeters(1), Units.feetToMeters(1)),
      new Translation2d(Units.feetToMeters(2), -Units.feetToMeters(1))
      ),
    //End 3 feet ahead of where robot start, facing forward
    new Pose2d(Units.feetToMeters(3), 0, new Rotation2d(0)),
    //Pass config
    config
    );
    
  private static final Trajectory autoTrajectory3 = TrajectoryGenerator.generateTrajectory(
    //Start at origin facing the +X direction
    new Pose2d(0, 0, new Rotation2d(0)),
    //Wavepoints making a S curve path
    List.of(
      new Translation2d(Units.feetToMeters(1), Units.feetToMeters(1)),
      new Translation2d(Units.feetToMeters(2), -Units.feetToMeters(1))
      ),
    //End 3 feet ahead of where robot start, facing forward
    new Pose2d(Units.feetToMeters(3), 0, new Rotation2d(0)),
    //Pass config
    config
    );

  
  public static final RamseteCommand ramseteCommand = new RamseteCommand(
    autoTrajectory,
    tankDriveSubsystem::getPose,
    new RamseteController(Constants.kRamseteB, Constants.kRamseteZeta),
    new SimpleMotorFeedforward(Constants.ksVolts,
                                Constants.kvVoltSecondsPerMeter,
                                Constants.kaVoltSecondsSquaredPerMeter),
    Constants.kDriveKinematics,
    tankDriveSubsystem::getWheelSpeeds,
    new PIDController(Constants.kPDriveVel, 0, 0),
    new PIDController(Constants.kPDriveVel, 0, 0),
    //RamseteCommand passes volts to the callback
    tankDriveSubsystem::tankDriveVolts,
    tankDriveSubsystem
  );

  public static final RamseteCommand ramseteCommand2 = new RamseteCommand(
    autoTrajectory2,
    tankDriveSubsystem::getPose,
    new RamseteController(Constants.kRamseteB, Constants.kRamseteZeta),
    new SimpleMotorFeedforward(Constants.ksVolts,
                                Constants.kvVoltSecondsPerMeter,
                                Constants.kaVoltSecondsSquaredPerMeter),
    Constants.kDriveKinematics,
    tankDriveSubsystem::getWheelSpeeds,
    new PIDController(Constants.kPDriveVel, 0, 0),
    new PIDController(Constants.kPDriveVel, 0, 0),
    //RamseteCommand passes volts to the callback
    tankDriveSubsystem::tankDriveVolts,
    tankDriveSubsystem
  );

  public static final RamseteCommand ramseteCommand3 = new RamseteCommand(
    autoTrajectory3,
    tankDriveSubsystem::getPose,
    new RamseteController(Constants.kRamseteB, Constants.kRamseteZeta),
    new SimpleMotorFeedforward(Constants.ksVolts,
                                Constants.kvVoltSecondsPerMeter,
                                Constants.kaVoltSecondsSquaredPerMeter),
    Constants.kDriveKinematics,
    tankDriveSubsystem::getWheelSpeeds,
    new PIDController(Constants.kPDriveVel, 0, 0),
    new PIDController(Constants.kPDriveVel, 0, 0),
    //RamseteCommand passes volts to the callback
    tankDriveSubsystem::tankDriveVolts,
    tankDriveSubsystem
  );
*/
  /**
   * The container for the robot.  Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    


    double ramptime = 1.5;
    NeutralMode coast = NeutralMode.Coast;


    middleLeft.configClosedloopRamp(ramptime);
    middleRight.configClosedloopRamp(ramptime);

    middleLeft.configOpenloopRamp(ramptime);
    middleRight.configOpenloopRamp(ramptime);

    rearLeft.configClosedloopRamp(ramptime);
    rearRight.configClosedloopRamp(ramptime);

    rearLeft.configOpenloopRamp(ramptime);
    rearRight.configOpenloopRamp(ramptime);
    
    frontLeft.configClosedloopRamp(ramptime);
    frontRight.configClosedloopRamp(ramptime);
        
    frontLeft.configOpenloopRamp(ramptime);
    frontRight.configOpenloopRamp(ramptime);


    middleLeft.setNeutralMode(coast);
    middleRight.setNeutralMode(coast);

    rearLeft.setNeutralMode(coast);
    rearRight.setNeutralMode(coast);
    
    frontRight.setNeutralMode(coast);
    frontLeft.setNeutralMode(coast);

    
    RobotContainer.frontLeft.follow(RobotContainer.middleLeft);
    RobotContainer.rearLeft.follow(RobotContainer.middleLeft);
  
    RobotContainer.frontLeft.setInverted(true);
    RobotContainer.rearLeft.setInverted(true);
    
    RobotContainer.frontRight.follow(RobotContainer.middleRight);
    RobotContainer.rearRight.follow(RobotContainer.middleRight); 

    RobotContainer.frontRight.setInverted(true);
    RobotContainer.rearRight.setInverted(true);
    
    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings.  Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a
   * {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
   // tankDriveButton.whenPressed(new MoveDistance(tankDriveSubsystem, 36));

    GearShiftButton.whileHeld(new GearShiftCommand(m_gearshiftsubsystem), true);
    

    leftjoystickbutton.whileActiveOnce(new PneumaticsToggle(m_pneumaticssubsytem));
    rightjoystickbutton.whileActiveOnce(new PneumaticsToggle(m_pneumaticssubsytem));
    shooterButton.whileHeld(new RunShooter(shooterSub, Constants.shooterTargetRPM));

    //BothUp.whileHeld(new BothPUpCommand(m_pneumaticssubsytem));
    //BothDown.whileHeld(new BothDownCommand(m_pneumaticssubsytem));

    forwardIndexerButton.whileHeld(new ManualIndexer(sensor, 0.5));
    reverseIndexerButton.whileHeld(new ManualIndexer(sensor, -0.5));

    //indexerIntakeButton.whileHeld(new ManualIndexer(sensor, -0.5));
    indexerIntakeButton.whileHeld(new ManualIntake(m_intakeSub, sensor, -0.7));

    limelightToggleButton.whenPressed(new ToggleLimelight(limelight));
    alignButton.whileHeld(new Align(limelight, tankDriveSubsystem));


  }


  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {

    return Robot.m_chooser.getSelected();
  }
}

