package tictactoe.util;

import java.util.Map;

import cn.nukkit.utils.TextFormat;
import tictactoe.Plugin;

import static java.util.Map.entry;

public class Localization {

	private static Map<String, String> stringTableEng = Map.ofEntries(
			entry("misc.loaded", "TicTacToe plugin loaded!"),
			entry("misc.enabled", "TicTacToe plugin enabled!"),
			entry("misc.max_dimension_size", "Max dimension size: %d"),
			entry("misc.disabled", "TicTacToe plugin (for Nukkit) disabled! Cancelling all games..."),
			entry("misc.error.command_execution_only_by_players", "This command may only be executed by players!"),
			
			entry("ttt.error.too_many_args", "Too many arguments for this command."),
			entry("ttt.error.expected_number", TextFormat.RED + "Error: expected number at '%s'." + TextFormat.RESET),
			entry("ttt.invitation.confirm", "You've just asked " + TextFormat.AQUA + TextFormat.BOLD + "%s" + TextFormat.RESET + " to play a game of tic-tac-toe with you!"),
			entry("ttt.invitation.revoked", TextFormat.AQUA + "" + TextFormat.BOLD + "%s" + TextFormat.RESET + " has revoked their tic-tac-toe-game request to you."),
			entry("ttt.invitation.updated", TextFormat.AQUA + "" + TextFormat.BOLD + "%s" + TextFormat.RESET + " has updated their tic-tac-toe-game request. See below."),
			entry("ttt.invitation.opponent_not_found", "The requested opponent player '%s' was not found on this server."),
			entry("ttt.invitation.main_is_opponent", "You cannot play a game with yourself ('%s')."),
			entry("ttt.returnRequest.confirm", "You've just asked " + TextFormat.AQUA + TextFormat.BOLD + "%s" + TextFormat.RESET + " to play a return match with you!"),
			
			entry("ttta.uuid_not_found", TextFormat.RED + "This game is not available anymore." + TextFormat.RESET),
			entry("ttta.request_not_found", TextFormat.RED + "'%s' hasn't sent any game request to you!" + TextFormat.RESET),
			
			
			entry("config.error.main_player_null", "Couldn't add you to the game. Please retry!"),
			entry("config.error.opponent_player_null", "Couldn't add the opponent player to the game."),
			entry("config.error.main_already_in_a_game", "You are currently playing a game of tic-tac-toe and, thus, cannot start another one."),
			entry("config.error.opponent_already_in_a_game", "'%s' is already playing tic-tac-toe with '%s'!"),
			entry("config.error.win_required_amount_too_small", "The required win amount must not be smaller than %d"),
			entry("config.error.win_required_amount_too_large", "The required win amount must not be larger than the size's largest dimension (%d)."),
			
			entry("game.invitation.return", "Hello " + TextFormat.AQUA + TextFormat.BOLD + "%s" + TextFormat.RESET + "! " + TextFormat.AQUA + TextFormat.BOLD + "%s" + TextFormat.RESET + " would like to play a return match with you!"),
			entry("game.invitation.normal", "Hello " + TextFormat.AQUA + TextFormat.BOLD + "%s" + TextFormat.RESET + "! " + TextFormat.AQUA + TextFormat.BOLD + "%s" + TextFormat.RESET + " would like to play a game of tic-tac-toe with you!"),
			entry("game.invitation.info", "It has a size of (" + TextFormat.BOLD + "%d" + TextFormat.RESET + ", " + TextFormat.BOLD + "%d" + TextFormat.RESET + ", " + TextFormat.BOLD + "%d" + TextFormat.RESET + ") and you need " + TextFormat.BOLD + "%d" + TextFormat.RESET + " fields in a row to win!"),
			entry("game.invitation.accept", "Execute " + TextFormat.BOLD + "/tictactoeaccept %s" + TextFormat.RESET + " to accept the game!"),
			entry("game.invitation.accepted", TextFormat.AQUA + "" + TextFormat.BOLD + "%s" + TextFormat.RESET + " has accepted your game!"),
			entry("game.invitation.not_available_anymore", TextFormat.AQUA + "" + TextFormat.BOLD + "%s" + TextFormat.RESET + " has just accepted another game. They cannot join yours anymore."),
			entry("game.invitation.not_available_anymore_hosted_own", TextFormat.AQUA + "" + TextFormat.BOLD + "%s" + TextFormat.RESET + " has just started their own game of tic-tac-toe. They cannot join yours anymore."),
			
			entry("game.cancelled", "Your current game of tic-tac-toe was " + TextFormat.YELLOW + TextFormat.BOLD + "cancelled" + TextFormat.RESET + "!"),
			entry("game.win.title", "You " + TextFormat.GREEN + TextFormat.BOLD + "won" + TextFormat.RESET + TextFormat.WHITE + " the game!"),
			entry("game.win.subtitle", "Good job!"),
			entry("game.lose.title", "You " + TextFormat.RED + TextFormat.BOLD + "lost" + TextFormat.RESET + TextFormat.WHITE + " the game!"),
			entry("game.lose.subtitle", "Never give up!"),
			entry("game.lose.returnRequestHelp", "Execute " + TextFormat.BOLD + "/tictactoe requestReturnMatch" + TextFormat.RESET + " to request a return match."),
			entry("game.tie.title", TextFormat.YELLOW + "Tie" + TextFormat.RESET),
			entry("game.tie.subtitle", "This game ended with a " + TextFormat.YELLOW + TextFormat.BOLD + "tie" + TextFormat.RESET + TextFormat.WHITE + "!"),
			entry("game.tie.returnRequestHelp", "Execute " + TextFormat.BOLD + "/tictactoe requestReturnMatch" + TextFormat.RESET + " to request another game.")
	);
	
	private static Map<String, String> stringTableDeu = Map.ofEntries(
			entry("misc.loaded", "TicTacToe-Plugin geladen!"),
			entry("misc.enabled", "TicTacToe-Plugin aktiviert!"),
			entry("misc.max_dimension_size", "Maximale Dimensionengröße: %d"),
			entry("misc.disabled", "TicTacToe-Plugin (für Nukkit) deaktiviert! Alle aktiven Spiele werden abgebrochen..."),
			entry("misc.error.command_execution_only_by_players", "Dieser Befehl kann nur von Spielern ausgeführt werden!"),
			
			entry("ttt.error.too_many_args", "Zu viele Argumente für diesen Befehl gegeben."),
			entry("ttt.error.expected_number", TextFormat.RED + "Feher: Zahl bei '%s' erwartet." + TextFormat.RESET),
			entry("ttt.invitation.confirm", "Du hast " + TextFormat.AQUA + TextFormat.BOLD + "%s" + TextFormat.RESET + " soeben zu einem Tic-Tac-Toe-Spiel eingeladen!"),
			entry("ttt.invitation.revoked", TextFormat.AQUA + "" + TextFormat.BOLD + "%s" + TextFormat.RESET + " hat seine / ihre Spieleinladung an Dich zurückgezogen."),
			entry("ttt.invitation.updated", TextFormat.AQUA + "" + TextFormat.BOLD + "%s" + TextFormat.RESET + " hat seine / ihre Spieleinladung an Dich aktualisiert. Siehe unten."),
			entry("ttt.invitation.opponent_not_found", "Der Spieler '%s' wurde auf diesem Server nicht gefunden."),
			entry("ttt.invitation.main_is_opponent", "Du kannst kein Spiel gegen Dich selbst ('%s') spielen."),
			entry("ttt.returnRequest.confirm", "Du hast " + TextFormat.AQUA + TextFormat.BOLD + "%s" + TextFormat.RESET + " soeben nach einem Revanche-Spiel gefragt!"),
			
			entry("ttta.uuid_not_found", TextFormat.RED + "Dieses Spiel ist nicht mehr verfügbar." + TextFormat.RESET),
			entry("ttta.request_not_found", TextFormat.RED + "'%s' hat Dir keine Spieleinladung gesendet!" + TextFormat.RESET),
			
			entry("config.error.main_player_null", "Du konntest dem Spiel nicht hinzugefügt werden. Bitte versuche es erneut!"),
			entry("config.error.opponent_player_null", "Der Gegner konnte dem Spile nicht hinzugefügt werden."),
			entry("config.error.main_already_in_a_game", "Du spielst bereits ein Tic-Tac-Toe-Spiel, weshalb Du kein weiteres starten kannst."),
			entry("config.error.opponent_already_in_a_game", "'%s' spielt bereits Tic-Tac-Toe mit '%s' und ist momentan nicht verfügbar!"),
			entry("config.error.win_required_amount_too_small", "Die zum Gewinnen benötigte Anzahl an Feldern darf nicht kleiner als %d sein"),
			entry("config.error.win_required_amount_too_large", "Die zum Gewinnen benötigte Anzahl an Feldern darf die größte Dimension des Spiels (%d) nicht überschreiten."),
			
			entry("game.invitation.return", "Hallo " + TextFormat.AQUA + TextFormat.BOLD + "%s" + TextFormat.RESET + "! " + TextFormat.AQUA + TextFormat.BOLD + "%s" + TextFormat.RESET + " würde gerne ein Revanche-Spiel mit Dir spielen!!"),
			entry("game.invitation.normal", "Hallo " + TextFormat.AQUA + TextFormat.BOLD + "%s" + TextFormat.RESET + "! " + TextFormat.AQUA + TextFormat.BOLD + "%s" + TextFormat.RESET + " würde gerne Tic-Tac-Toe mit Dir spielen!"),
			entry("game.invitation.info", "Es hat die Größe (" + TextFormat.BOLD + "%d" + TextFormat.RESET + ", " + TextFormat.BOLD + "%d" + TextFormat.RESET + ", " + TextFormat.BOLD + "%d" + TextFormat.RESET + ") und es werden " + TextFormat.BOLD + "%d" + TextFormat.RESET + " Felder in einer Reihe benötigt, um zu gewinnen!"),
			entry("game.invitation.accept", "Führe " + TextFormat.BOLD + "/tictactoeaccept %s" + TextFormat.RESET + " aus, um die Einladung anzunehmen!"),
			entry("game.invitation.accepted", TextFormat.AQUA + "" + TextFormat.BOLD + "%s" + TextFormat.RESET + " hat Deine Einladung angenommen!"),
			entry("game.invitation.not_available_anymore", TextFormat.AQUA + "" + TextFormat.BOLD + "%s" + TextFormat.RESET + " hat gerade eine andere Einladung angenommen und kann Deine Einladung nicht mehr akzeptieren."),
			entry("game.invitation.not_available_anymore_hosted_own", TextFormat.AQUA + "" + TextFormat.BOLD + "%s" + TextFormat.RESET + " hat soeben sein / ihr eigenes Tic-Tac-Toe-Spiel begonnen und kann Deine Einladung nicht mehr akzeptieren."),
			
			entry("game.cancelled", "Dein aktuelles Tic-Tac-Toe-Spiel wurde " + TextFormat.YELLOW + TextFormat.BOLD + "abgebrochen" + TextFormat.RESET + "!"),
			entry("game.win.title", "Du hast " + TextFormat.GREEN + TextFormat.BOLD + "gewonnen" + TextFormat.RESET + TextFormat.WHITE + "!"),
			entry("game.win.subtitle", "Gut gemacht!"),
			entry("game.lose.title", "Du hast " + TextFormat.RED + TextFormat.BOLD + "verloren" + TextFormat.RESET + TextFormat.WHITE + "!"),
			entry("game.lose.subtitle", "Bloß nicht aufgeben!"),
			entry("game.lose.returnRequestHelp", "Führe " + TextFormat.BOLD + "/tictactoe requestReturnMatch" + TextFormat.RESET + " aus, um ein Revanche-Spiel anzufragen."),
			entry("game.tie.title", TextFormat.YELLOW + "Unentschieden" + TextFormat.RESET),
			entry("game.tie.subtitle", "Dieses Spiel endete " + TextFormat.YELLOW + TextFormat.BOLD + "ohne Sieger" + TextFormat.RESET + TextFormat.WHITE + "!"),
			entry("game.tie.returnRequestHelp", "Führe " + TextFormat.BOLD + "/tictactoe requestReturnMatch" + TextFormat.RESET + " aus, um ein weiteres Spiel anzufragen.")
	);
	
	private static Map<String, String> getCurrentLanguageTable() {
		return Plugin.instance.getServer().getLanguage().getLang().equals("deu") ? stringTableDeu : stringTableEng;
	}
	
	public static String localizedString(String identifier, Object... args) {
		String resolvedString = Localization.getCurrentLanguageTable().containsKey(identifier) ? Localization.getCurrentLanguageTable().get(identifier) : identifier;
		return String.format(resolvedString, args);
	}
	
}
