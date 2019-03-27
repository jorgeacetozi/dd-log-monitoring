package com.jorgeacetozi.dd.logmonitoring.handler;

import java.util.logging.Logger;

public class ConsoleHandler implements Handler {

  private static final Logger LOGGER = Logger.getLogger(ConsoleHandler.class.getName());

  @Override
  public void sendNotification(String message) {
    if (message != null && !message.isEmpty()) {
      LOGGER.info(message);
    }
  }

}
