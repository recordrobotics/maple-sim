package org.ironmaple.simulation.seasonspecific.heroheist;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import java.util.Arrays;
import java.util.List;
import org.ironmaple.simulation.SimulatedArena;
import org.ironmaple.utils.FieldMirroringUtils;

/**
 *
 *
 * <h1>The playing field for the 2025 WCP Cadathon FRC Game: Hero Heist</h1>
 *
 * <p>This class represents the playing field for the 2025 WCP Cadathon FRC game, Hero Heist.
 *
 * <p>It extends {@link SimulatedArena} and includes specific details of the Hero Heist game environment.
 */
public class ArenaHeroHeist extends SimulatedArena {
    public static final class HeroHeistFieldObstacleMap extends FieldMap {
        public HeroHeistFieldObstacleMap() {
            super();

            // blue wall
            super.addBorderLine(new Translation2d(0, 0), new Translation2d(0, 6.858));

            // west foothills districts
            super.addBorderLine(new Translation2d(0, 6.858), new Translation2d(1.634308, 8.2296));

            // red wall
            super.addBorderLine(new Translation2d(16.4592, 0), new Translation2d(16.4592, 6.858));

            // east foothills districts
            super.addBorderLine(new Translation2d(16.4592, 6.858), new Translation2d(16.4592 - 1.634308, 8.2296));

            // downtown walls
            super.addBorderLine(new Translation2d(0, 0), new Translation2d(4.55295, 0));
            super.addBorderLine(new Translation2d(16.4592, 0), new Translation2d(16.4592 - 4.55295, 0));

            // uptown walls
            super.addBorderLine(new Translation2d(1.634308, 8.2296), new Translation2d(4.572, 8.2296));
            super.addBorderLine(
                    new Translation2d(16.4592 - 1.634308, 8.2296), new Translation2d(16.4592 - 4.572, 8.2296));

            // downtown districts
            super.addRectangularObstacle(7.3152, 0.762, new Pose2d(16.4592 / 2, -0.3048 + 0.762 / 2, new Rotation2d()));

            // uptown districts
            super.addRectangularObstacle(
                    7.3152, 0.6858, new Pose2d(16.4592 / 2, 7.8486 + 0.6858 / 2, new Rotation2d()));

            // downtown polycarb dividers
            super.addRectangularObstacle(
                    0.01905, 1.3716, new Pose2d(4.55295 + 0.01905 / 2, 1.3716 / 2, new Rotation2d()));
            super.addRectangularObstacle(
                    0.01905, 1.3716, new Pose2d(16.4592 - (4.55295 + 0.01905 / 2), 1.3716 / 2, new Rotation2d()));
        }
    }

    public ArenaHeroHeist() {
        super(new HeroHeistFieldObstacleMap());
    }

    @Override
    public void placeGamePiecesOnField() {
        Translation2d[] blueStoryPanelPositions = new Translation2d[] {
            new Translation2d(0.3556, 2.2860),
            new Translation2d(0.3556, 3.2004),
            new Translation2d(0.3556, 4.1148),
            new Translation2d(0.3556, 5.0292),
            new Translation2d(0.3556, 5.9436),
        };
        for (Translation2d position : blueStoryPanelPositions)
            super.addGamePiece(new HeroHeistBlueStoryPanelOnField(position));

        Translation2d[] redStoryPanelPositions = Arrays.stream(blueStoryPanelPositions)
                .map(bluePosition ->
                        new Translation2d(FieldMirroringUtils.FIELD_WIDTH - bluePosition.getX(), bluePosition.getY()))
                .toArray(Translation2d[]::new);
        for (Translation2d position : redStoryPanelPositions)
            super.addGamePiece(new HeroHeistRedStoryPanelOnField(position));

        Translation2d[] blueSpeechBubblePositions = new Translation2d[] {
            new Translation2d(5.1816, 2.89551),
            new Translation2d(5.1816, 3.50511),
            new Translation2d(5.1816, 4.11471),
            new Translation2d(5.1816, 4.72431),
            new Translation2d(5.1816, 5.33391),
            new Translation2d(6.4008, 2.89551),
            new Translation2d(6.4008, 3.50511),
            new Translation2d(6.4008, 4.11471),
            new Translation2d(6.4008, 4.72431),
            new Translation2d(6.4008, 5.33391),
            new Translation2d(7.62, 2.89551),
            new Translation2d(7.62, 3.50511),
            new Translation2d(7.62, 4.11471),
            new Translation2d(7.62, 4.72431),
            new Translation2d(7.62, 5.33391)
        };
        for (Translation2d position : blueSpeechBubblePositions)
            super.addGamePiece(new HeroHeistBlueSpeechBubbleOnField(position));

        Translation2d[] redSpeechBubblePositions = Arrays.stream(blueSpeechBubblePositions)
                .map(bluePosition ->
                        new Translation2d(FieldMirroringUtils.FIELD_WIDTH - bluePosition.getX(), bluePosition.getY()))
                .toArray(Translation2d[]::new);
        for (Translation2d position : redSpeechBubblePositions)
            super.addGamePiece(new HeroHeistRedSpeechBubbleOnField(position));
    }

    @Override
    public synchronized List<Pose3d> getGamePiecesPosesByType(String type) {
        return super.getGamePiecesPosesByType(type);
    }

    @Override
    public synchronized void clearGamePieces() {
        super.clearGamePieces();
    }
}
