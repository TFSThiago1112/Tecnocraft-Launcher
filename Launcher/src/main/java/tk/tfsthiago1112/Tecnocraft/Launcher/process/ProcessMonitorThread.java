package tk.tfsthiago1112.Tecnocraft.Launcher.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;

public class ProcessMonitorThread extends Thread {

    static org.apache.logging.log4j.Logger log = LogManager.getLogger();
    private final JavaProcess process;

    public ProcessMonitorThread(JavaProcess process) {
        this.process = process;
    }

    @Override
    public void run() {
        InputStreamReader reader = new InputStreamReader(this.process.getRawProcess().getInputStream());
        BufferedReader buf = new BufferedReader(reader);
        String line = null;

        while (this.process.isRunning()) {
            try {
                while ((line = buf.readLine()) != null) {
                    log.debug("Client> " + line);
                    this.process.getSysOutLines().add(line);
                }
            } catch (IOException ex) {
                Logger.getLogger(ProcessMonitorThread.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    buf.close();
                } catch (IOException ex) {
                    Logger.getLogger(ProcessMonitorThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        JavaProcessRunnable onExit = this.process.getExitRunnable();

        if (onExit != null) {
            onExit.onJavaProcessEnded(this.process);
        }
    }
}
