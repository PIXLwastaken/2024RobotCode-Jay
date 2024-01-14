package frc.robot.Autos;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.PathPlannerLogging;
import com.pathplanner.lib.util.ReplanningConfig;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.Drivetrain.DrivetrainConfig;

/* MASTER AUTON CLASS */
public class AutonMaster {
    private static Field2d mGameField;
    private static SendableChooser<Command> mAutonChooser = new SendableChooser<>();

    public AutonMaster() { 

        configureNamedCommands();

        //Configuring AutoBuilder
        AutoBuilder.configureHolonomic(
                RobotContainer.mSwerveDrivetrain::getPose, 
                RobotContainer.mSwerveDrivetrain::resetPose, 
                RobotContainer.mSwerveDrivetrain::getRobotRelativeSpeeds, 
                RobotContainer.mSwerveDrivetrain::driveRobotRelative, 
                new HolonomicPathFollowerConfig( 
                        AutonConfig.TRANSLATION_PID, 
                        AutonConfig.ROTATION_PID,
                        AutonConfig.MAX_AUTON_MODULE_SPEED, 
                        AutonConfig.DRIVE_BASE_RADIUS, 
                        new ReplanningConfig() 
                ),
                () -> {
                    var alliance = DriverStation.getAlliance();
                    if (alliance.isPresent()) {
                        return alliance.get() == DriverStation.Alliance.Red;
                    }
                    return false;
                },
            RobotContainer.mSwerveDrivetrain 
        );

       configureAutoChooser(); 
       configurePathPlannerLogging();
    }
    
    private void configureAutoChooser() { 
        mAutonChooser = AutoBuilder.buildAutoChooser();
    }

    public static SendableChooser<Command> getAutonSelector() {
        return mAutonChooser;
    }

    private void configurePathPlannerLogging() {
        mGameField = new Field2d();
        SmartDashboard.putData("Field", mGameField);
        
        PathPlannerLogging.setLogCurrentPoseCallback((pose) -> {
            mGameField.setRobotPose(pose);
        });

        PathPlannerLogging.setLogTargetPoseCallback((pose) -> {
            mGameField.getObject("target pose").setPose(pose);
        });

        PathPlannerLogging.setLogActivePathCallback((poses) -> {
            mGameField.getObject("path").setPoses(poses);
        });
    }

    /* Named Commands are like the new "Event Map" */
    private void configureNamedCommands() {
        NamedCommands.registerCommand("ZeroHeading", new InstantCommand(() -> {
                                RobotContainer.mSwerveDrivetrain.gyro.setYaw(RobotContainer.mSwerveDrivetrain.getGyroYaw().getDegrees());}));
        
        NamedCommands.registerCommand("StopSwerve", new InstantCommand(RobotContainer.mSwerveDrivetrain::stopSwerve));
    }
}
