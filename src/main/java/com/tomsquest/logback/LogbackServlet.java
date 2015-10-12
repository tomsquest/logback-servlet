package com.tomsquest.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;

public class LogbackServlet extends HttpServlet {

    private static final String DEFAULT_LOGGER_NAME = "com.company";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        printLoggers(resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (isNotBlank(req.getParameter("reloadFromDisk"))) {
            reloadConfigFromDisk();
        } else {
            configureLogger(req.getParameter("loggerName"), req.getParameter("level"));
        }

        printLoggers(resp);
    }

    private void printLoggers(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<Logger> loggers = context.getLoggerList();

        PrintWriter writer = resp.getWriter();
        try {
            writer.write(buildHtml(loggers));
        } finally {
            writer.close();
        }
    }

    private void configureLogger(String loggerName, String level) {
        if (isNotBlank(loggerName) && isNotBlank(level)) {
            Logger logger = (Logger) LoggerFactory.getLogger(loggerName);
            logger.setLevel(Level.toLevel(level));
        }
    }

    private void reloadConfigFromDisk() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ContextInitializer ci = new ContextInitializer(loggerContext);
        URL url = ci.findURLOfDefaultConfigurationFile(true);

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            loggerContext.reset();
            configurator.doConfigure(url);
        } catch (JoranException ignored) {
        }

        StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
    }

    private boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private String buildHtml(List<Logger> loggers) {
        String style = "<style>th.sort-header::-moz-selection {background: transparent;}th.sort-header::selection {background: transparent;}th.sort-header {cursor: pointer;}th.sort-header::-moz-selection,th.sort-header::selection {background: transparent;}table th.sort-header:after {content: '';float: right;margin-top: 7px;border-width: 0 4px 4px;border-style: solid;border-color: #404040 transparent;visibility: hidden;}table th.sort-header:hover:after {visibility: visible;}table th.sort-up:after,table th.sort-down:after,table th.sort-down:hover:after {visibility: visible;opacity: 0.4;}table th.sort-up:after {border-bottom: none;border-width: 4px 4px 0;}</style>";
        String header = "<!DOCTYPE html><html><head><meta charset='utf-8'><link href='//cdnjs.cloudflare.com/ajax/libs/normalize/3.0.2/normalize.min.css' rel=stylesheet type='text/css'><link href='//cdnjs.cloudflare.com/ajax/libs/skeleton/2.0.4/skeleton.min.css' rel='stylesheet' type='text/css'><script type='text/javascript' src='//cdnjs.cloudflare.com/ajax/libs/tablesort/2.2.4/tablesort.min.js'></script><script type='text/javascript' src='//cdnjs.cloudflare.com/ajax/libs/list.js/1.1.1/list.min.js'></script>" + style + "</head><body><div class='container'>";
        String footer = "</div></body></html>";
        String configureLoggerForm = "<h5>Configuration</h5><div class='row'><form method='post'><div class='row'><div class='four columns'><input class='u-full-width' id='loggerName' name='loggerName' type='text' value='" + DEFAULT_LOGGER_NAME + "'></div><div class='two columns'><select class='u-full-width' id='level' name='level'><option value='all'>all</option><option value='trace'>trace</option><option value='debug' selected='selected'>debug</option><option value='info'>info</option><option value='warn'>warn</option><option value='error'>error</option><option value='off'>off</option></select></div><div class='three columns'><input class='button-primary' value='Reconfigure logger' type='submit'></div><div class='three columns'><input name='reloadFromDisk' value='Clear/Reload from disk !' type='submit'></div></div></form></div>";
        String loggersTableStart = "<h5>Loggers</h5><div id='loggers'><input type='text' class='search u-full-width' placeholder='Search'/><table id='loggers-table' class='u-full-width'><thead><tr><th>Logger</th><th>Level</th></tr></thead><tbody class='list'>";
        String loggersTableEnd = "<script>new Tablesort(document.getElementById('loggers-table'));new List('loggers', {valueNames: ['logger-name', 'logger-level']});</script></tbody></table></div>";

        String loggersTableRows = "";
        String lineSeparator = System.getProperty("line.separator");
        for (Logger logger : loggers) {
            loggersTableRows += "<tr><td class='logger-name'>" + logger.getName() + "</td><td class='logger-level'>" + logger.getEffectiveLevel() + "</td></tr>" + lineSeparator;
        }

        return header + configureLoggerForm + loggersTableStart + loggersTableRows + loggersTableEnd + footer;
    }
}