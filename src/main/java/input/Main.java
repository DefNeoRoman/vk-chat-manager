package input;

import analyzer.CensorAnalyzer;
import analyzer.ChatMessagesAnalyzer;
import analyzer.ChatUsersPresenceAnalyzer;
import auth.AuthManager;
import auth.AutoAuthManager;
import auth.ManualAuthManager;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import config.Config;
import model.ChatUsersActivity;
import model.ChatUsersPresence;
import operator.VkChatOperator;
import report.FileReporter;

import java.util.Arrays;
import java.util.List;

public class Main {
	//redirect uri https://oauth.vk.com/blank.html
	public static void main(String[] args) throws Exception {
		
		if (!Config.init(Constants.CONFIG_FILE)) {
			Config.createDefaultConfig(Constants.CONFIG_FILE);
			System.err.println("Default config file created: "+Constants.CONFIG_FILE);
			System.err.println("Please fill it with your settings before the next run");
			return;
		}
		
		TransportClient tc = HttpTransportClient.getInstance();
		VkApiClient vk = new VkApiClient(tc);
		AuthManager auth;
		if (Config.EMAIL.isEmpty() || Config.PASS.isEmpty()) {
			System.out.println("Email/password missing, applying manual authentication...");
			auth = new ManualAuthManager(Config.CLIENT_ID, Config.CLIENT_SECRET);
		} else {
			System.out.println("Email/password provided, applying automatic authentication...");
			auth = new AutoAuthManager(Config.CLIENT_ID, Config.CLIENT_SECRET, Config.EMAIL, Config.PASS);
		}
		
		System.out.println();
		
		UserActor actor = auth.getUserActor(vk, Constants.SCOPE);
		
		VkChatOperator op = new VkChatOperator(vk, actor);
		FileReporter reporter = new FileReporter(op);
		
		if (Config.isReportRequired("activity")) {
			ChatMessagesAnalyzer cma = new ChatMessagesAnalyzer(op);
			for (int chatId : Config.CHATS) {
				System.out.println("Collecting users activity model for chat "+chatId);
				ChatUsersActivity cua = cma.getActiveAndPassiveUsers(chatId, Config.REPORT_DAYS);
				System.out.println("Making a report...");
				reporter.reportActiveAndPassiveUsers(cua);
				System.out.println("Done");
				System.out.println();
			}
		}

		if (Config.isReportRequired("presence")) {
			System.out.println("Collecting users presence model for chats "+Arrays.toString(Config.CHATS));
			ChatUsersPresenceAnalyzer cupa = new ChatUsersPresenceAnalyzer(op);
			ChatUsersPresence cup = cupa.getUsersPresence(Config.CHATS);
			System.out.println("Making a report...");
			reporter.reportUsersPresence(cup);
			System.out.println("Done");
			System.out.println();
		}

		if (Config.isReportRequired("censor")) {
			System.out.println("Collecting not censor users in chat "+Arrays.toString(Config.CHATS));
			CensorAnalyzer censorAnalyzer = new CensorAnalyzer(op);
			for (int chatId : Config.CHATS) {
				System.out.println("Collecting users activity model for chat "+chatId);
				List<Integer> ids = censorAnalyzer.getNotCensorUsersIds(chatId, Config.REPORT_DAYS);
				System.out.println("Making a report...");
				reporter.reportNotCensorUsers(ids);
			}

			System.out.println("Done");
			System.out.println();
		}
		System.out.println("Check reports directory for results");
	}	
}
