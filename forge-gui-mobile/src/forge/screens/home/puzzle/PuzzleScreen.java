package forge.screens.home.puzzle;

import forge.GuiBase;
import forge.assets.FSkinFont;
import forge.deck.Deck;
import forge.game.GameRules;
import forge.game.GameType;
import forge.game.player.RegisteredPlayer;
import forge.match.HostedMatch;
import forge.player.GamePlayerUtil;
import forge.puzzle.Puzzle;
import forge.puzzle.PuzzleIO;
import forge.screens.LaunchScreen;
import forge.screens.LoadingOverlay;
import forge.screens.home.NewGameMenu;
import forge.toolbox.FLabel;
import forge.toolbox.FTextArea;
import forge.toolbox.GuiChoose;
import forge.util.Callback;
import forge.util.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PuzzleScreen extends LaunchScreen {

    private static final float PADDING = Utils.scale(10);

    private final FTextArea lblDesc = add(new FTextArea(false,
            "Puzzle Mode loads in a puzzle that you have to win in a predetermined time/way.\n\n" +
            "To begin, press the Start button below, then select a puzzle from a list.\n\n" +
            "Your objective will be specified on a special effect card which will be placed in your command zone."));

    public PuzzleScreen() {
        super(null, NewGameMenu.getMenu());

        lblDesc.setFont(FSkinFont.get(12));
        lblDesc.setTextColor(FLabel.INLINE_LABEL_COLOR);
    }

    @Override
    protected void doLayoutAboveBtnStart(float startY, float width, float height) {
        float x = PADDING;
        float y = startY + PADDING;
        float w = width - 2 * PADDING;
        float h = height - y - PADDING;
        lblDesc.setBounds(x, y, w, h);
    }

    @Override
    protected void startMatch() {
        final ArrayList<Puzzle> puzzles = PuzzleIO.loadPuzzles();
        Collections.sort(puzzles);

        GuiChoose.one("Choose a puzzle", puzzles, new Callback<Puzzle>() {
            @Override
            public void run(Puzzle chosen) {
                LoadingOverlay.show("Loading the puzzle...", new Runnable() {
                    @Override
                    public void run() {
                        // Load selected puzzle
                        final HostedMatch hostedMatch = GuiBase.getInterface().hostMatch();
                        hostedMatch.setStartGameHook(new Runnable() {
                            @Override
                            public final void run() {
                                chosen.applyToGame(hostedMatch.getGame());
                            }
                        });

                        final List<RegisteredPlayer> players = new ArrayList<>();
                        final RegisteredPlayer human = new RegisteredPlayer(new Deck()).setPlayer(GamePlayerUtil.getGuiPlayer());
                        human.setStartingHand(0);
                        players.add(human);

                        final RegisteredPlayer ai = new RegisteredPlayer(new Deck()).setPlayer(GamePlayerUtil.createAiPlayer());
                        ai.setStartingHand(0);
                        players.add(ai);

                        GameRules rules = new GameRules(GameType.Puzzle);
                        rules.setGamesPerMatch(1);
                        hostedMatch.startMatch(rules, null, players, human, GuiBase.getInterface().getNewGuiGame());
                    }
                });
            }
        });

    }
}
