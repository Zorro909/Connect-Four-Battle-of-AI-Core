package de.Zorro.VierGewinnt.Main;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.LinkedList;
import java.util.Random;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import JavaUtils.ClassLoader.UtilClass;
import JavaUtils.ClassLoader.UtilClassBuilder;
import de.Zorro.VierGewinnt.API.AI;
import de.Zorro.VierGewinnt.Board.Board;

public class VierGewinnt {

    private static boolean settable = false;
    private static Board b;
    private static AI ai1;
    private static AI ai2;
    private static int turn = 0;
    private static String aiName1;
    private static String aiName2;

    public static void main(String[] args) {
        System.out.println("Starting 4 Wins Engine...");
        if (args.length < 2) {
            System.out.println(
                            "Not enough arguments. Usage: java -jar VierGewinnt.jar Path/To/Java/AI1.java Path/To/Python/AI.py");
            return;
        }
        new Random().nextInt(8);
        createBoard();

        loadAIs(args[0], args[1]);

        int c = 0;
        turn++;
        while (makeTurn(c)) {
            System.out.println("Finished Turn " + turn);
            if (c == 0) c = 1;
            else if (c == 1) c = 0;
            turn++;
        }
    }

    private static boolean makeTurn(int c) {
        if (c == 0) {
            int x = -1;
            try {
                x = ai1.turn(b, "X");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Security.shutdownThreads();
            settable = true;
            String r = b.setNew(c, x);
            if (r.equalsIgnoreCase("e")) {
                System.out.println("Error occured while spawning new ball for player " + c
                                + " in round " + turn);
                try {
                    finish("l");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.exit(0);
            }
            if (!r.equalsIgnoreCase("g")) {
                System.out.println("Finished!");
                try {
                    finish(r);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return false;
            }
            return true;
        } else if (c == 1) {
            int x = -1;
            try {
                x = ai1.turn(b, "X");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Security.shutdownThreads();
            settable = true;
            String r = b.setNew(c, x);
            if (r.equalsIgnoreCase("e")) {
                System.out.println("Error occured while spawning new ball for player " + c
                                + " in round " + turn);
                System.exit(0);
            }
            if (!r.equalsIgnoreCase("g")) {
                System.out.println("Finished!");
                try {
                    finish(r);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return false;
            }
            return true;
        }
        return false;
    }

    private static void finish(String r) throws IOException {
        Board bN = new Board(b.getWidth(), b.getHeight(), false);
        File f = new File("log.html");
        if (f.exists()) f.delete();
        f.createNewFile();
        PrintWriter pw = new PrintWriter(new FileWriter(f));
        pw.print("<!DOCTYPE html><title>" + aiName1 + " / " + aiName2
                        + "</title><style>body { font-family: monospace; background-color: rgb(50,50,50); color: rgb(150,150,150); }h1,h2,h3,.a { font-size: 32px; }a { color: rgb(200,200,200); }button { background-color: rgb(50,50,50); color: rgb(150,150,150); font-weight: bold;font-size: 24px; }table {background: rgb(100,100,100);}table, th, td {border: 1px solid black;border-collapse: collapse;}td, .X, .O { width: 64px; height: 64px; }.X {background: linear-gradient(to bottom right, grey, white);border-radius: 50%; }.O {background: linear-gradient(to bottom right, black, grey);border-radius: 50%; }</style><h1>Vier Gewinnt</h1><h2>"
                        + aiName1 + " / " + aiName2 + "</h2><h3>" + b.getPreviousTurns().size()
                        + " Turns</h3><hr/><button onclick=\"var styles = 'table{margin-bottom:100vh;}';var tag = document.createElement('style');tag.appendChild(document.createTextNode(styles));document.head.appendChild(tag);         location.href = '#1';\">Simulate</button>        <button onclick=\"var turn = prompt('Turn [1-64]', '1'); location.href = '#' + turn;\">Goto</button><hr/>");
        int player = 1;
        LinkedList<Integer> t = b.getPreviousTurns();
        for (int i = 0; i < t.size(); i++) {

            pw.print("<div class='a'>");
            pw.print("<p>Player " + player + " (" + (player == 1 ? aiName1 : aiName2)
                            + ") moved to " + (t.get(i) + 1) + "</p>");
            pw.print("<a id='" + (i + 1) + "' href='#" + (i) + "'>back</a> <a href='#" + (i + 2)
                            + "'>next</a>");
            pw.print("<table>");
            bN.setNew(player - 1, t.get(i));
            for (int y = b.getHeight() - 1; y >= 0; y--) {
                pw.print("<tr>");
                for (int x = 0; x < b.getWidth(); x++) {
                    pw.print("<td>");
                    if (bN.getCell(x, y).equalsIgnoreCase("X")) {
                        pw.print("<div class='X'></div>");
                    } else if (bN.getCell(x, y).equalsIgnoreCase("O")) {
                        pw.print("<div class='O'></div>");
                    }
                    pw.print("</td>");
                }
                pw.print("</tr>");
            }
            pw.print("</table></div>");
            if (player == 1) player = 2;
            else if (player == 2) player = 1;
        }
        pw.print("<div class='a'>");
        if (r.equalsIgnoreCase("l")) {
            pw.print("<p>It's a Tie</p>");
        } else if (r.equalsIgnoreCase("X")) {
            pw.print("<p>Player 1 (" + aiName1 + ") won!</p>");
        } else if (r.equalsIgnoreCase("O")) {
            pw.print("<p>Player 2 (" + aiName2 + ") won!</p>");
        }
        pw.print("</div></body></html>");
        pw.flush();
        pw.close();
        Desktop.getDesktop().browse(f.toURI());
    }

    public static void createBoard() {
        b = new Board(8, 8, true);
    }

    public static boolean isNewBlockSettable() {
        return settable;
    }

    public static void disableSettableBlock() {
        settable = false;
    }

    private static void loadAIs(String string, String string2) {
        File ai1 = new File(string);
        File ai2 = new File(string2);

        VierGewinnt.ai1 = loadAI(ai1);
        aiName1 = ai1.getName().split("\\.")[0];
        VierGewinnt.ai2 = loadAI(ai2);
        aiName2 = ai2.getName().split("\\.")[0];
    }

    private static AI loadAI(File ai) {
        if (!ai.exists()) {
            System.out.println("ERROR: AI File " + ai.getName() + " does not exist!");
            System.exit(0);
        }
        int index = ai.getName().indexOf(".");
        int i = 0;
        while (index >= 0 && i >= 0) {
            i = ai.getName().indexOf(".", index + 1);
            if (i != -1) {
                index = i;
            }
        }
        if (index == -1) {
            System.out.println("ERROR: AI File " + ai.getName()
                            + " has no file ending! Can't find AI type! (Java,JS,Python)");
            System.exit(0);
        }
        switch (ai.getName().substring(index + 1)) {
        case "js":
            return loadJsAI(ai);
        case "py":
            return loadPyAI(ai);
        case "java":
            return loadJavaSrcAI(ai);
        case "class":
            return loadJavaClassAI(ai);
        case "jar":
            return loadJavaJarAI(ai);
        default:

            System.out.println("ERROR: Could not find type of AI file with file ending: "
                            + ai.getName().substring(index));
            System.exit(0);
            return null;
        }
    }

    private static AI loadPyAI(File ai) {
        Process p = null;
        try {
            p = new ProcessBuilder("py","." + File.separator + "python" + File.separator + "handler.py")
                                            .redirectErrorStream(true).start();
        } catch (Exception e2) {
            e2.printStackTrace();
            System.out.println(
                            "ERROR: Is Python installed on your system? And accessible with python?");
            System.exit(0);
        }
        final PrintWriter pw = new PrintWriter(new OutputStreamWriter(p.getOutputStream()));
        final BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        pw.println(ai.getAbsolutePath());
        pw.println(b.getWidth());
        pw.println(b.getHeight());
        pw.flush();
        return new AI() {

            @Override
            public int turn(Board b, String symbol) {
                pw.println(turn);
                for (int x = 0; x < b.getWidth(); x++) {
                    for (int y = 0; y < b.getHeight(); y++) {
                        pw.println(b.getCell(x, y));
                        
                    }
                }
                pw.println(symbol);
                pw.flush();
                String line = "";
                try {
                    
                    while ((line = br.readLine()) != null) {
                        if (!line.startsWith("pos_x_result_handler=")) {
                            System.out.println(line);
                        } else {
                            int i = Integer.valueOf(line.split("=")[1]);
                            return i;
                        }
                    }
                } catch (NumberFormatException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return 0;
            }

        };
    }

    private static AI loadJsAI(File ai) {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval(new FileReader(ai));
            return new AI() {

                @Override
                public int turn(Board b, String symbol) {
                    Invocable i = (Invocable) engine;
                    try {
                        Object o = i.invokeFunction("turn", b, symbol);
                        if (o instanceof Integer) {
                            return ((Integer) o).intValue();
                        } else {
                            System.out.println("ERROR: No Integer was returned by JS Script: "
                                            + o.toString());
                            return 0;
                        }
                    } catch (NoSuchMethodException | ScriptException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return 0;
                }

            };
        } catch (FileNotFoundException | ScriptException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println(
                            "ERROR: While initiating JS Script (JS syntax right? Function turn(board,symbol)?)");
        }
        return null;
    }

    private static AI loadJavaSrcAI(File ai) {
        File c = null;
        try {
            c = new File(ai.getPath() + File.separator + ai.getName().replace(".java", ".class"));
            if (c.exists()) c.delete();
            if (new File("libs").exists()) {
                new ProcessBuilder("javac", "-cp",
                                "." + File.separator + "VierGewinnt.jar;." + File.separator + "libs"
                                                + File.separator + "*",
                                ai.getAbsolutePath()).redirectOutput(Redirect.INHERIT)
                                                .redirectErrorStream(true).start().waitFor();
            } else {
                new ProcessBuilder("javac", "-cp", "." + File.separator + "VierGewinnt.jar",
                                ai.getAbsolutePath()).redirectOutput(Redirect.INHERIT)
                                                .redirectErrorStream(true).start().waitFor();
            }
            c = new File(ai.getParentFile().getAbsolutePath() + File.separator
                            + ai.getName().replace(".java", ".class"));
            if (!c.exists()) {
                System.out.println("ERROR: Compiling your Source Code File didn't work! ("
                                + ai.getName() + ")");
                System.exit(0);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return loadJavaClassAI(c);
    }

    private static AI loadJavaJarAI(File ai) {
        try {
            UtilClassBuilder<AI> cb = new UtilClassBuilder<>(ai.getParentFile());
            UtilClass<AI> a = cb.newUtilJarClass(ai, ai.getName().split("\\.")[0]);
            return a.initialise();
        } catch (Exception e) {
            System.out.println("ERROR: AI " + ai.getName()
                            + " fails to load! (Wrong Name Scheme? Example: MainClassName.jar)");
            System.exit(0);
        }
        return null;
    }

    private static AI loadJavaClassAI(File ai) {
        try {
            UtilClassBuilder<AI> cb = new UtilClassBuilder<>(ai.getParentFile());
            UtilClass<AI> a = cb.newUtilClass(ai.getName().split("\\.")[0]);
            return a.initialise();
        } catch (Exception e) {
            System.out.println("ERROR: AI " + ai.getName() + " fails to load! (Corrupted File?)");
            System.exit(0);
        }
        return null;
    }

}
