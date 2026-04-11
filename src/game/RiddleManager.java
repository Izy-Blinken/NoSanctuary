package game;
import models.Riddle;

public class RiddleManager {
    panel gp;
    public Riddle[] riddles = new Riddle[3];
    public int solvedCount = 0;
    public boolean portalSpawned = false;
    private static final int WRONG_PENALTY_TICKS = 300;

    public RiddleManager(panel gp) {
        this.gp = gp;
        initRiddles();
    }

    public void initRiddles() {
        solvedCount = 0;
        portalSpawned = false;
        int level = gp.tileM.currentLevel;

        if (level == 3) {
            riddles[0] = new Riddle(0,
                "I stand in the earth, I reach for the sky. I give without moving, I live till I die. What am I?",
                "tree",
                "It stands without moving, yet it gives to all who pass."
            );
            riddles[1] = new Riddle(1,
                "I grow in the quiet, I hang from the tall. I feed those who hunger, I sweeten the fall. What am I?",
                "apple",
                "Sweet and still. It waits on the one that lives till it dies."
            );
            riddles[2] = new Riddle(2,
                "What sweetens the fall hangs from what lives till it dies. Seek the one that still bears what the forest denies.",
                "",
                ""
            );
        } else if (level == 2) {
            riddles[0] = new Riddle(0,
                "I mirror your every move without a sound. I vanish at nightfall, I live on the ground. What am I?",
                "shadow",
                "It follows without feet. It disappears when the dark arrives."
            );
            riddles[1] = new Riddle(1,
                "I was built by the living, now claimed by the earth. I stand without purpose, long past my worth. What am I?",
                "ruin",
                "The living built it. The forest took it back."
            );
            riddles[2] = new Riddle(2,
                "Where your mirror fades darkest, the forgotten still stand. The way out is buried beneath crumbled hand.",
                "",
                ""
            );
        } else {
            riddles[0] = new Riddle(0,
                "I am spent by the living, stolen by the dead. I cannot be bought back, only lost instead. What am I?",
                "time",
                "It runs without legs. The dead take it first."
            );
            riddles[1] = new Riddle(1,
                "I leave when the body falls cold to the ground. I am sought by the living but cannot be found. What am I?",
                "soul",
                "It departs in silence. Only the graves remember where it went."
            );
            riddles[2] = new Riddle(2,
                "Where time runs out and souls no longer linger, the forest parts for those who dare. Follow where both have already left.",
                "",
                ""
            );
        }
    }

    public boolean isThirdRiddleUnlocked() {
        return riddles[0].solved && riddles[1].solved;
    }

    public boolean tryAnswer(int riddleIndex, String rawAnswer) {
        if (riddleIndex < 0 || riddleIndex >= riddles.length) return false;
        // third riddle has no answer
        if (riddleIndex == 2) return false;
        Riddle r = riddles[riddleIndex];
        if (r.solved) return true;
        if (rawAnswer.toLowerCase().trim().equals(r.answer)) {
            r.solved = true;
            solvedCount++;
            appendClueToScroll(r);
            if (solvedCount >= 2) appendThirdRiddleToScroll();
            return true;
        } else {
            applyWrongAnswerPenalty();
            return false;
        }
    }

    public Riddle getRiddle(int index) {
        if (index < 0 || index >= riddles.length) return null;
        return riddles[index];
    }

    public boolean isAllSolved() {
        return solvedCount >= 2;
    }

    private void applyWrongAnswerPenalty() {
        if (gp.dC.currentState == dayCounter.dayNightState.Day) {
            gp.dC.stateCounter -= WRONG_PENALTY_TICKS;
            if (gp.dC.stateCounter < 0) gp.dC.stateCounter = 0;
        }
    }

    private void appendClueToScroll(Riddle r) {
        
        String existing = gp.inventory.clueText == null ? "" : gp.inventory.clueText;
        if (!existing.contains(r.clue)) {
            gp.inventory.clueText = existing.isEmpty() ? r.clue : existing + "\n\n" + r.clue;
            gp.inventory.hasClue = true;
        }
        if (solvedCount >= 2) {
            spawnPortal();
        }
    }

    private void appendThirdRiddleToScroll() {
        Riddle r = riddles[2];
        String existing = gp.inventory.clueText == null ? "" : gp.inventory.clueText;
        String entry = "~ " + r.question + " ~";
        if (!existing.contains(entry)) {
            gp.inventory.clueText = existing.isEmpty() ? entry : existing + "\n\n" + entry;
            gp.inventory.hasClue = true;
        }
    }

    private void spawnPortal() {
        if (portalSpawned) return;
        portalSpawned = true;
        gp.objectM.revealPortal();
    }
}