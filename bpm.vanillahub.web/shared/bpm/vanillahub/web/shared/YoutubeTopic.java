package bpm.vanillahub.web.shared;

import java.util.ArrayList;
import java.util.List;

public class YoutubeTopic {
	
	public enum TopicParent {
		MUSIC_TOPICS(0),
		GAMING_TOPICS(1),
		SPORTS_TOPICS(2),
		ENTERTAINMENT_TOPICS(3),
		LIFESTYLE_TOPICS(4),
		SOCIETY_TOPICS(5),
		OTHER_TOPICS(6);
		
		private int code;
		
		private TopicParent(int code) {
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}
	}
	
	public enum Topic {
		MUSIC_PARENT_TOPIC(TopicParent.MUSIC_TOPICS, "/m/04rlf"),
		CHRISTIAN_MUSIC(TopicParent.MUSIC_TOPICS, "/m/02mscn"),
		CLASSICAL_MUSIC(TopicParent.MUSIC_TOPICS, "/m/0ggq0m"),
		COUNTRY(TopicParent.MUSIC_TOPICS, "/m/01lyv"),
		ELECTRONIC_MUSIC(TopicParent.MUSIC_TOPICS, "/m/02lkt"),
		HIP_HOP_MUSIC(TopicParent.MUSIC_TOPICS, "/m/0glt670"),
		INDEPENDENT_MUSIC(TopicParent.MUSIC_TOPICS, "/m/05rwpb"),
		JAZZ(TopicParent.MUSIC_TOPICS, "/m/03_d0"),
		MUSIC_OF_ASIA(TopicParent.MUSIC_TOPICS, "/m/028sqc"),
		MUSIC_OF_LATIN_AMERICA(TopicParent.MUSIC_TOPICS, "/m/0g293"),
		POP_MUSIC(TopicParent.MUSIC_TOPICS, "/m/064t9"),
		REGGAE(TopicParent.MUSIC_TOPICS, "/m/06cqb"),
		RHYTHM_AND_BLUES(TopicParent.MUSIC_TOPICS, "/m/06j6l"),
		ROCK_MUSIC(TopicParent.MUSIC_TOPICS, "/m/06by7"),
		SOUL_MUSIC(TopicParent.MUSIC_TOPICS, "/m/0gywn"),
		
		GAMING_PARENT_TOPIC(TopicParent.GAMING_TOPICS, "/m/0bzvm2"),
		ACTION_GAME(TopicParent.GAMING_TOPICS, "/m/025zzc"),
		ACTION_ADVENTURE_GAME(TopicParent.GAMING_TOPICS, "/m/02ntfj"),
		CASUAL_GAME(TopicParent.GAMING_TOPICS, "/m/0b1vjn"),
		MUSIC_VIDEO_GAME(TopicParent.GAMING_TOPICS, "/m/02hygl"),
		PUZZLE_VIDEO_GAME(TopicParent.GAMING_TOPICS, "/m/04q1x3q"),
		RACING_VIDEO_GAME(TopicParent.GAMING_TOPICS, "/m/01sjng"),
		ROLE_PLAYING_VIDEO_GAME(TopicParent.GAMING_TOPICS, "/m/0403l3g"),
		SIMULATION_VIDEO_GAME(TopicParent.GAMING_TOPICS, "/m/021bp2"),
		SPORTS_GAME(TopicParent.GAMING_TOPICS, "/m/022dc6"),
		STRATEGY_VIDEO_GAME(TopicParent.GAMING_TOPICS, "/m/03hf_rm"),
		
		SPORTS_PARENT_TOPIC(TopicParent.SPORTS_TOPICS, "/m/06ntj"),
		AMERICAN_FOOTBALL(TopicParent.SPORTS_TOPICS, "/m/0jm_"),
		BASEBALL(TopicParent.SPORTS_TOPICS, "/m/018jz"),
		BASKETBALL(TopicParent.SPORTS_TOPICS, "/m/018w8"),
		BOXING(TopicParent.SPORTS_TOPICS, "/m/01cgz"),
		CRICKET(TopicParent.SPORTS_TOPICS, "/m/09xp_"),
		FOOTBALL(TopicParent.SPORTS_TOPICS, "/m/02vx4"),
		GOLF(TopicParent.SPORTS_TOPICS, "/m/037hz"),
		ICE_HOCKEY(TopicParent.SPORTS_TOPICS, "/m/03tmr"),
		MIXED_MARTIAL_ARTS(TopicParent.SPORTS_TOPICS, "/m/01h7lh"),
		MOTORSPORT(TopicParent.SPORTS_TOPICS, "/m/0410tth"),
		TENNIS(TopicParent.SPORTS_TOPICS, "/m/07bs0"),
		VOLLEYBALL(TopicParent.SPORTS_TOPICS, "/m/07_53"),
		
		ENTERTAINMENT_PARENT_TOPIC(TopicParent.ENTERTAINMENT_TOPICS, "/m/02jjt"),
		HUMOR(TopicParent.ENTERTAINMENT_TOPICS, "/m/09kqc"),
		MOVIES(TopicParent.ENTERTAINMENT_TOPICS, "/m/02vxn"),
		PERFORMING_ARTS(TopicParent.ENTERTAINMENT_TOPICS, "/m/05qjc"),
		PROFESSIONAL_WRESTLING(TopicParent.ENTERTAINMENT_TOPICS, "/m/066wd"),
		TV_SHOWS(TopicParent.ENTERTAINMENT_TOPICS, "/m/0f2f9"),
		
		LIFESTYLE_PARENT_TOPIC(TopicParent.LIFESTYLE_TOPICS, "/m/019_rr"),
		FASHION(TopicParent.LIFESTYLE_TOPICS, "/m/032tl"),
		FITNESS(TopicParent.LIFESTYLE_TOPICS, "/m/027x7n"),
		FOOD(TopicParent.LIFESTYLE_TOPICS, "/m/02wbm"),
		HOBBY(TopicParent.LIFESTYLE_TOPICS, "/m/03glg"),
		PETS(TopicParent.LIFESTYLE_TOPICS, "/m/068hy"),
		PHYSICAL_ATTRACTIVENESS_BEAUTY(TopicParent.LIFESTYLE_TOPICS, "/m/041xxh"),
		TECHNOLOGY(TopicParent.LIFESTYLE_TOPICS, "/m/07c1v"),
		TOURISM(TopicParent.LIFESTYLE_TOPICS, "/m/07bxq"),
		VEHICLES(TopicParent.LIFESTYLE_TOPICS, "/m/07yv9"),
		
		
		SOCIETY_PARENT_TOPIC(TopicParent.LIFESTYLE_TOPICS, "/m/098wr"),
		BUSINESS(TopicParent.LIFESTYLE_TOPICS, "/m/09s1f"),
		HEALTH(TopicParent.LIFESTYLE_TOPICS, "/m/0kt51"),
		MILITARY(TopicParent.LIFESTYLE_TOPICS, "/m/01h6rj"),
		POLITICS(TopicParent.LIFESTYLE_TOPICS, "/m/05qt0"),
		RELIGION(TopicParent.LIFESTYLE_TOPICS, "/m/06bvp"),
	
		KNOWLEDGE(TopicParent.OTHER_TOPICS, "/m/01k8wb");
		
		private TopicParent parent;
		private String code;
		
		private Topic(TopicParent parent, String code) {
			this.parent = parent;
			this.code = code;
		}
		
		public TopicParent getParent() {
			return parent;
		}
		
		public String getCode() {
			return code;
		}
	}
	
	public static TopicParent getTopicParent(int code) {
		for (TopicParent parent : TopicParent.values()) {
			if (parent.getCode() == code) {
				return parent;
			}
		}
		return null;
	}
	
	public static List<Topic> getTopicsByParent(TopicParent parent) {
		List<Topic> topics = new ArrayList<>();
		for (Topic topic : Topic.values()) {
			if (topic.getParent() == parent) {
				topics.add(topic);
			}
		}
		return topics;
	}
	
	public static Topic getTopicByCode(String code) {
		for (Topic topic : Topic.values()) {
			if (topic.getCode().equals(code)) {
				return topic;
			}
		}
		return null;
	}
}
