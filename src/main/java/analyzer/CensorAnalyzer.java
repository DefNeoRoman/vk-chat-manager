package analyzer;


import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import model.User;
import operator.VkChatOperator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static input.Utils.daysAgo;

public class CensorAnalyzer {
    private VkChatOperator operator;
    private Set<String> vocabulary = new HashSet<>();
    public CensorAnalyzer(VkChatOperator operator) {
        this();
        this.operator = operator;
    }

    public CensorAnalyzer(){
        try {
            URL resource = CensorAnalyzer.class.getResource("/vocabulary.txt");
          File file =  Paths.get(resource.toURI()).toFile();
            Files.lines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8).forEach(
                    (line) -> {
                        vocabulary.add(line);
                    });
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }


    public List<Integer> getNotCensorUsersIds(int chatId, int daysToCheck) throws Exception {

        List<Message> message = operator.getMessagesAfter(daysAgo(daysToCheck), chatId);
        AtomicInteger countOfWords = new AtomicInteger(0);
        AtomicInteger countOfWarnings = new AtomicInteger(0);
        List <Integer> warningList = new ArrayList<>();
       message.stream().forEach(m->{
           String nov = m.getBody().replaceAll("\\p{Punct}","");
           String[] forSearch = nov.split(" ");
           for (int i=0; i<forSearch.length;i++){
              if(vocabulary.contains(forSearch[i])){
                warningList.add(m.getFromId());
                  countOfWarnings.incrementAndGet();
              }
              countOfWords.incrementAndGet();
           }


       });
        System.out.println("общее"+countOfWords);
        System.out.println("notCensored"+countOfWarnings);


        return warningList;
    }

}
