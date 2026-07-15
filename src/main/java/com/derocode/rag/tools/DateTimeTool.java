package com.derocode.rag.tools;

import org.apache.logging.log4j.CloseableThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


@Component
public class DateTimeTool {

    private static final Logger log = LoggerFactory.getLogger(DateTimeTool.class);

    @Tool(description = """
Returns the actual current date and time.
Use this tool whenever the user asks about
the current date, current time, today,
tomorrow, yesterday, or any relative time
such as 'in 10 minutes', 'next Tuesday',
'two hours from now', etc.
""")

    String getCurrentDateTime() {
        String now = LocalDateTime.now()
                .truncatedTo(ChronoUnit.SECONDS)
                .toString();

        log.info("Returning: {}", now);

        return now;
    }
}
