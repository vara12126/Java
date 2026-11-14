# Question 5: Generics and Sports Leaderboard

## 1. AIM
To design, implement, and verify a reusable generic sports leaderboard application (`Leaderboard<T>`) employing functional Java `Comparator` composition (`Comparator.comparing()` and `thenComparing()`), enforce multi-tiered tie-breaking rules, explain why Bisons is ranked above Panthers, test edge scenarios and flawed comparators, and analyze bounded generic wildcards.

---

## 2. PROBLEM STATEMENT
A sports tournament league requires a generic ranking engine capable of sorting competition entities (`Team`) according to strict hierarchy:
1. **Rule 1**: Higher accumulated tournament points first (Descending).
2. **Rule 2**: Higher goal difference first (Descending).
3. **Rule 3**: Alphabetical order of team name (Ascending).

The program must:
- Complete both supplied TODO sections.
- Strictly produce the exact console output:
  `[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]`
- Defensively protect the underlying registration list from mutation during ranking.
- Thoroughly defend why Bisons takes precedence over Panthers.
- Contrast `Comparable` vs `Comparator` and explain bounded wildcards (`? super T`, `? extends T`).

---

## 3. THEORY

### 3.1 Comparator Composition
Modern Java provides functional builder combinators for `java.util.Comparator`:
```java
Comparator<Team> teamComparator = Comparator
    .comparingInt(Team::getPoints).reversed()
    .thenComparing(Comparator.comparingInt(Team::getGoalDifference).reversed())
    .thenComparing(Team::getName);
```
- `comparingInt(Team::getPoints).reversed()` establishes primary ordering descending.
- `thenComparing(...)` is evaluated if and only if the primary comparator evaluates to `0` (tie).
- Chaining guarantees deterministic sorting without nested conditional logic.

### 3.2 Defensive Programming
Mutating a collection passed into a method or sorting an internal collection in place exposes callers to side effects (e.g. concurrent modification or altered registration order). Defensive copying:
```java
List<T> rankedCopy = new ArrayList<>(this.items);
rankedCopy.sort(comparator);
return Collections.unmodifiableList(rankedCopy);
```
ensures that `items` remains untouched and the caller receives an immutable snapshot.

### 3.3 Comparable vs Comparator
- **`Comparable<T>`**: Defines the natural, intrinsic ordering of an object via `int compareTo(T o)`. Embedded within the entity class; only one natural ordering can exist.
- **`Comparator<T>`**: Defines an external comparison strategy via `int compare(T o1, T o2)`. Facilitates multiple, dynamic, interchangeable sorting criteria without altering entity code.

### 3.4 Bounded Generics & The PECS Principle
- **PECS**: "Producer Extends, Consumer Super".
- **`addAll(List<? extends T> items)`**: The list *produces* data into the leaderboard, allowing subtypes of `T` to be added safely.
- **`ranking(Comparator<? super T> comp)`**: The comparator *consumes* instances of `T` to compare them, allowing comparators defined on superclasses of `T` (such as `Object`) to be applied.

---

## 4. ALGORITHM

### 4.1 Ranking Algorithm
1. Receive input `Team` records:
   - Cobras (6, 1)
   - Panthers (9, 4)
   - Eagles (9, 6)
   - Bisons (9, 4)
2. Evaluate comparator rules pairwise:
   - Primary: $P_B - P_A$ (Points descending).
   - Secondary: $GD_B - GD_A$ (Goal Difference descending).
   - Tertiary: $\text{Name}_A.\text{compareTo}(\text{Name}_B)$ (Lexicographical ascending).
3. TimSort executes in $O(N \log N)$ time.
4. Return immutable ranked view.

---

## 5. IMPLEMENTATION DETAILS

### 5.1 Structure
```text
Q5_Generics_Leaderboard/
├── src/
│   ├── Team.java                   # Domain entity
│   ├── Leaderboard.java            # Generic Leaderboard<T>
│   └── LeaderboardApp.java         # Application with TODOs complete
├── screenshots/
│   └── Q5_Leaderboard_Output.png   # Console output verification
└── README.md                       # Technical report
```

---

## 6. PROGRAM CODE
The complete source files are located in the [src/](file:///C:/Users/varap/.gemini/antigravity/scratch/Java/Assignment%202/Q5_Generics_Leaderboard/src) directory:
- [Team.java](file:///C:/Users/varap/.gemini/antigravity/scratch/Java/Assignment%202/Q5_Generics_Leaderboard/src/Team.java)
- [Leaderboard.java](file:///C:/Users/varap/.gemini/antigravity/scratch/Java/Assignment%202/Q5_Generics_Leaderboard/src/Leaderboard.java)
- [LeaderboardApp.java](file:///C:/Users/varap/.gemini/antigravity/scratch/Java/Assignment%202/Q5_Generics_Leaderboard/src/LeaderboardApp.java)

---

## 7. INPUT
Input teams registered into `Leaderboard<Team>`:
```java
leagueLeaderboard.add(new Team("Cobras", 6, 1));
leagueLeaderboard.add(new Team("Panthers", 9, 4));
leagueLeaderboard.add(new Team("Eagles", 9, 6));
leagueLeaderboard.add(new Team("Bisons", 9, 4));
```

---

## 8. OUTPUT
Exact console output:
```text
Original Unsorted Registration List (Preserved Defensively):
  [Cobras(6,1), Panthers(9,4), Eagles(9,6), Bisons(9,4)]

Final Official Ranked Output (Exact Required Output):
[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]
```

---

## 9. EXECUTION SCREENSHOTS
![Q5_Leaderboard_Output](file:///C:/Users/varap/.gemini/antigravity/scratch/Java/Assignment%202/Q5_Generics_Leaderboard/screenshots/Q5_Leaderboard_Output.png)

---

## 10. EXPERIMENTAL RESULTS

### 10.1 Mathematical Proof: Why Bisons is Ranked Above Panthers
1. **Eagles(9,6)**:
   - Points: 9 | Goal Difference: +6.
   - Clears all other teams on Goal Difference. **Rank 1**.
2. **Bisons(9,4) vs. Panthers(9,4)**:
   - **Rule 1 (Points)**: $\text{Points}_{\text{Bisons}} = 9 == \text{Points}_{\text{Panthers}} = 9 \implies \text{TIE}$.
   - **Rule 2 (Goal Difference)**: $\text{GD}_{\text{Bisons}} = +4 == \text{GD}_{\text{Panthers}} = +4 \implies \text{TIE}$.
   - **Rule 3 (Alphabetical Order)**:
     $$\text{"Bisons".compareTo("Panthers")}$$
     - Character comparison at index 0:
       $$\text{'B'} (\text{ASCII } 66) - \text{'P'} (\text{ASCII } 80) = -14 < 0$$
     - In lexicographical order, `"Bisons"` strictly precedes `"Panthers"`.
   - **Conclusion**: Bisons is definitively ranked above Panthers (**Rank 2 vs Rank 3**).
3. **Cobras(6,1)**:
   - Points: 6. Lowest points in tournament. **Rank 4**.

---

## 11. TIME COMPLEXITY
- **TimSort**: Dual-Pivot Quicksort / TimSort (`List.sort()`) guarantees:
  - Best Case: $O(N)$ (Already sorted).
  - Average Case: $O(N \log N)$.
  - Worst Case: $O(N \log N)$.
- Comparison overhead per pair is $O(1)$ primitive comparisons and small string prefix checks.

---

## 12. SPACE COMPLEXITY
- Defensive copy creates an auxiliary `ArrayList<T>` of size $N \implies O(N)$ space.
- Original list remains completely unaltered, preventing side-effect bugs across threads.

---

## 13. ALTERNATIVE CONCEPT: Intentional Flawed Comparator Experiment
To illustrate the danger of incomplete comparators, we tested:
```java
Comparator<Team> flawed = Comparator.comparingInt(Team::getPoints);
```
Output produced:
`[Cobras(6,1), Panthers(9,4), Eagles(9,6), Bisons(9,4)]`
- **Flaw 1**: Ascending order mistakenly made Cobras (the lowest-scoring team) the champion.
- **Flaw 2**: With no tie-breaker, Panthers and Bisons were ordered arbitrarily based on insertion order.

---

## 14. ANALYSIS & ARCHITECTURAL DEFENSE
- The application of `Comparator.thenComparing()` provides compile-time type safety and high readability.
- The use of bounded wildcards `Comparator<? super T>` conforms strictly to the Java Collections Framework architecture, ensuring maximum component reusability.

---

## 15. RESULT
Question 5 was fully implemented, both TODOs completed, exact output verified, tiebreakers mathematically proven, and genuine execution screenshots recorded.
