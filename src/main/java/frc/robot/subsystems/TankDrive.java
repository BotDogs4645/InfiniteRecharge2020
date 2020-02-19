package frc.robot.subsystems;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;
import frc.robot.commands.DriveCommand;
import edu.wpi.first.wpilibj.smartdashboard.*;
import edu.wpi.first.wpilibj.util.Units;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;



public class TankDrive extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
  public final PIDController pid = new PIDController(1, 0, 0);

  //Multidimensional gyroscope and accelerometer sensor
  private final AHRS ahrs = new AHRS();

  //Odometry class for tracking robot pose
  private final DifferentialDriveOdometry m_odometry;

  //Diameter of tankDriveWheels in meters
  private final double wheelDiameter = Units.inchesToMeters(6);
  
  public TankDrive() {
    setDefaultCommand(new DriveCommand(this));
    pid.setTolerance(128); //Error is within 1/8 of a revolution
    

    resetEncoders();
    m_odometry = new DifferentialDriveOdometry(Rotation2d.fromDegrees(getHeading()));
  }


  public void driveWithJoystick() {
    //ONE JOYSTICK
    //make sure throttle is at 1 or -1
    double forward = (RobotContainer.stick.getY())*.8;
    double turn = (RobotContainer.stick.getZ())*.8;

    /*deadband*/
    
    if ((Math.abs(forward)<0.05) && (Math.abs(turn)<0.05))
    {
      stop();
    }

    else 
    {
      RobotContainer.difDrive.arcadeDrive(forward, turn);
    }
    
    
  }
  
  

  public void stop() {
    RobotContainer.difDrive.arcadeDrive(0, 0);
  }
  


  public void driveDistance(double distance) {
    double count = -distance * (1024 / (6 * Math.PI));
    RobotContainer.middleLeft.setSelectedSensorPosition(0);
    pid.setSetpoint(count);
    
    
  }
  public void updateDrive() {
    double output = pid.calculate(RobotContainer.middleLeft.getSelectedSensorPosition(), pid.getSetpoint());
    SmartDashboard.putNumber("count", pid.getSetpoint());
    SmartDashboard.putNumber("current count", RobotContainer.middleLeft.getSelectedSensorPosition());

    
    if (output > 0.4) {
      output = 0.4;
    }
    if (output < -0.4) {
      output = -0.4;
    }
    RobotContainer.difDrive.arcadeDrive(output, 0);
  }
  
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    m_odometry.update(Rotation2d.fromDegrees(getHeading()), 
    getActualDistance(RobotContainer.middleLeft), 
    getActualDistance(RobotContainer.middleRight));
  }

  /**
   * Returns the currently-estimated pose of the robot.
   *
   * @return The pose.
   */
  public Pose2d getPose() {
    return m_odometry.getPoseMeters();
  }

  /**
   * Returns the current wheel speeds of the robot.
   *
   * @return The current wheel speeds.
   */
  public DifferentialDriveWheelSpeeds getWheelSpeeds() {
    return new DifferentialDriveWheelSpeeds(getActualRate(RobotContainer.middleLeft), getActualRate(RobotContainer.middleRight));
  }

  /**
   * Resets the odometry to the specified pose.
   *
   * @param pose The pose to which to set the odometry.
   */
  public void resetOdometry(Pose2d pose) {
    resetEncoders();
    m_odometry.resetPosition(pose, Rotation2d.fromDegrees(getHeading()));
  }

   /**
   * Controls the left and right sides of the drive directly with voltages.
   *
   * @param leftVolts  the commanded left output
   * @param rightVolts the commanded right output
   */
  public void tankDriveVolts(double leftVolts, double rightVolts) {
    RobotContainer.leftSide.setVoltage(leftVolts);
    RobotContainer.rightSide.setVoltage(rightVolts);
    RobotContainer.difDrive.feed();
  }

  /**
   * Resets the drive encoders to currently read a position of 0.
   */
  public void resetEncoders() {
    RobotContainer.middleLeft.setSelectedSensorPosition(0);
    RobotContainer.middleRight.setSelectedSensorPosition(0);
  }

   /**
   * Gets the average distance of the two encoders.
   *
   * @return the average of the two encoder readings
   */
  public double getAverageEncoderDistance() {
    return (getActualDistance(RobotContainer.middleLeft) + getActualDistance(RobotContainer.middleRight)) / 2.0;
  }

  /**
   * Sets the max output of the drive.  Useful for scaling the drive to drive more slowly.
   *
   * @param maxOutput the maximum output to which the drive will be constrained
   */
  public void setMaxOutput(double maxOutput) {
    RobotContainer.difDrive.setMaxOutput(maxOutput);
  }

  /**
   * Zeroes the heading of the robot.
   */
  public void zeroHeading() {
    ahrs.reset();
  }

  /** 
   * Gets the actual distance in meters traveled by the robot based on encoder values
   * 
   * @param motor The TalonSRX motor with an encoder
   * 
   * @return The distance traveled by the robot in meters 
  */
  public double getActualDistance(WPI_TalonSRX motor) {
    double count = motor.getSelectedSensorPosition();
    return ((wheelDiameter * Math.PI)/1024) * count;
  }

  /**
   * Gets the actual rate in meters per second traveled by the robot
   * 
   * @param motor The TalonSRX motor with an encoder
   * 
   * @return The rate at which the robot travels in meters per second
   */
  public double getActualRate(WPI_TalonSRX motor) {
    double encoderRate = motor.getSelectedSensorVelocity(); //Returns count per 100ms
    return (10 * encoderRate * wheelDiameter * Math.PI) / 1024.0;
  }

   /**
   * Returns the heading of the robot.
   *
   * @return the robot's heading in degrees, from -180 to 180
   */
   public double getHeading() {
     return ahrs.getYaw();
   }
   
   /**
   * Returns the turn rate of the robot.
   *
   * @return The turn rate of the robot, in degrees per second
   */
  public double getTurnRate() {
    return ahrs.getRate();
  }

}