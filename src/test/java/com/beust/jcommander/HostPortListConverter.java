package com.beust.jcommander;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Scott Stark
 * @version $Revision:$
 */
public class HostPortListConverter implements IStringConverter<List<HostPort>> {
   @Override
   public List<HostPort> convert(String value) {
      ArrayList<HostPort> hps = new ArrayList<HostPort>();
      // First split on ; to find host/port pairs
      String[] pairs = value.split(";");
      for (String pair : pairs) {
         String[] s = pair.split(":");
         HostPort result = new HostPort();
         result.host = s[0];
         result.port = Integer.parseInt(s[1]);
         hps.add(result);
      }
      return hps;
   }
}
