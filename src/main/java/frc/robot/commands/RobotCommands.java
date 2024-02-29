package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.elevator.ElevatorConfig.ElevatorState;
import frc.robot.elevator.commands.SetElevatorState;
import frc.robot.indexer.Indexer;
import frc.robot.indexer.commands.IndexerCommands;
import frc.robot.intakepivot.commands.IntakePivotCommands;
import frc.robot.intakepivot.commands.SetStatePivot;
import frc.robot.intakeroller.commands.IntakeCommands;

public class RobotCommands {
    public static Command handOff() {
        return new SequentialCommandGroup(
            new ParallelCommandGroup(
                new SetStatePivot(SetStatePivot.State.kDeployed),
                new SetElevatorState(ElevatorState.kZero)
                /*Add setShooterState */
            ),
            new ParallelRaceGroup(
                IndexerCommands.loadNote(),
                new SequentialCommandGroup(
                    new WaitCommand(0.5),
                    IntakeCommands.setOutput(() -> -1)
                )
            )
        );
    }

    public static Command spitNote() {
        return new SequentialCommandGroup(
            IntakePivotCommands.setPivotState(SetStatePivot.State.kSpit),
            IntakeCommands.setOutput(() -> 1)
        );
    }

    public static Command primeAmpShooter() {
        return new SequentialCommandGroup(
            new ConditionalCommand(null, handOff(), () -> Indexer.getInstance().hasNote())
            /*Add Shooter set Pivot for Amp */
        );
    }

    public static Command primeSpeakerShooter() {
        return new SequentialCommandGroup(
            new ConditionalCommand(null, handOff(), () -> Indexer.getInstance().hasNote()),
            new ParallelCommandGroup(
                /*Add set Angle for shooter */
                /*Add Set RPM for Shooter */
            )
        );
    }

    public static Command shootNoteSpeaker() {
        return new SequentialCommandGroup(
            new ParallelRaceGroup(
                /*Add set RPM for Shooter */
                new SequentialCommandGroup(
                    /*Runs Indexer for some time */
                    new WaitCommand(1)
                )
            )
        );
    }

    public static Command shooterNoteAmp() {
        return new SequentialCommandGroup(
            new ParallelRaceGroup(
                /*Run shooter at (spec. velocity) */
                new WaitCommand(0.5),
                new SequentialCommandGroup(
                    new ParallelRaceGroup(

                        new WaitCommand(1)
                    )
                )
            )
        );
    }
}
