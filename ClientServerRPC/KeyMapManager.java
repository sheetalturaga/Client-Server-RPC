package ClientServerRPC;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 *  The Implementation of add, retrieving or deleting the key-value provided by the client
 *  is managed by this class.
 */

public class KeyMapManager {

  public ServerLogger serverLogger = new ServerLogger();
  public String keyMap;

  public KeyMapManager(String keyMap) {
    this.keyMap = keyMap;
  }

  public String presentInMap(String key) throws IOException {
    Properties properties = new Properties();
    String keyValueFileName = new File("").getAbsolutePath();
    keyValueFileName = keyValueFileName + File.pathSeparator + keyMap;
    File keyValueFile = new File(keyValueFileName);
    keyValueFile.createNewFile();
    FileInputStream fileStream = new FileInputStream(keyValueFile);
    properties.load(fileStream);
    String valueToRetrieve = properties.getProperty(key);
    fileStream.close();
    return valueToRetrieve;
  }

  public boolean implementPutInMap(String key, String value) throws IOException {
    boolean putComplete = false;
    String keyValueFileName = new File("").getAbsolutePath();
    keyValueFileName = keyValueFileName + File.pathSeparator + keyMap;
    File keyValueFile = new File(keyValueFileName);
    keyValueFile.createNewFile();
    BufferedReader bufferedReader = new BufferedReader(new FileReader(keyValueFile));
    String input = "";
    List list = new ArrayList<>();
    int countOfKeys = 0;

    while ((input = bufferedReader.readLine()) != null) {
      if(input.contains(key)) {
        input += " : " + value;
        countOfKeys++;
      }
      list.add(input);
    }

    if(countOfKeys == 0) {
      list.add(key += " : " + value);
    }
    bufferedReader.close();
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(keyValueFile));
    Iterator iterator = list.iterator();
    while (iterator.hasNext()) {
      input = (String) iterator.next();
      bufferedWriter.write(input);
      bufferedWriter.newLine();
    }
    bufferedReader.close();
    String valToReturn = presentInMap(key);
    if (valToReturn.contains(value)) {
      System.out.println("The Value to return: "+ valToReturn);
      putComplete = true;
    }

    return putComplete;
  }

  public String implementDeleteInMap(String key) throws IOException {
    String value = presentInMap(key);
    String output = "";
    if(value.isEmpty()) {
      output = "No such key is present in the Map";
    } else {
      String keyValueFileName = new File("").getAbsolutePath();
      keyValueFileName = keyValueFileName + File.pathSeparator + keyMap;
      File keyValueFile = new File(keyValueFileName);
      keyValueFile.createNewFile();
      BufferedReader bufferedReader = new BufferedReader(new FileReader(keyValueFile));
      String input = "";
      List list = new ArrayList<>();
      while ((input = bufferedReader.readLine()) != null) {
        if (input.contains(key)) {
          list.add(input);
        }
      }
      bufferedReader.close();
      BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(keyValueFile));
      Iterator iterator = list.iterator();
      while (iterator.hasNext()) {
        input = (String) iterator.next();
        bufferedWriter.write(input);
        bufferedWriter.newLine();
      }
      bufferedReader.close();
      output = "The provided key: " + key + "has been deleted from Map";
    }
    return output;
  }
}
