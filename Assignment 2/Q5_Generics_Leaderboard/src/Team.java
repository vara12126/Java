package q5_leaderboard;

import java.util.Objects;

/**
 * Domain model representing a sports team participating in a league competition.
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class Team {
    private final String name;
    private final int points;
    private final int goalDifference;

    public Team(String name, int points, int goalDifference) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Team name cannot be null or empty.");
        }
        this.name = name.trim();
        this.points = points;
        this.goalDifference = goalDifference;
    }

    public String getName() { return name; }
    public int getPoints() { return points; }
    public int getGoalDifference() { return goalDifference; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team)) return false;
        Team team = (Team) o;
        return points == team.points &&
               goalDifference == team.goalDifference &&
               Objects.equals(name, team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, points, goalDifference);
    }

    /**
     * Exact required representation: Name(Points,GoalDifference)
     * e.g., "Eagles(9,6)"
     */
    @Override
    public String toString() {
        return String.format("%s(%d,%d)", name, points, goalDifference);
    }
}
