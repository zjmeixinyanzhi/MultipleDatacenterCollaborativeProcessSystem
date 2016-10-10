package Pbs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class PbsQueueInfo {
	private String[] queueSort;

	public String[] getQueueSort() {
		return queueSort;
	}

	public PbsQueueInfo() {
		PriorityQueue<PbsQueue> pbs = new PriorityQueue<PbsQueue>();
		Map<String, String> queue = new HashMap<String, String>();
		try {
			String cmd = "qstat -Qf";
			Process pro = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(pro
					.getInputStream()));
			String line = in.readLine();
			while (line != null) {
				if (line.trim().startsWith("Queue:")) {
					// get queue name
					String[] keyValue = line.split(":");
					queue.put(keyValue[0].trim(), keyValue[1].trim());
					line = in.readLine();
					while (!line.trim().isEmpty()
							&& !line.trim().startsWith("Queue:")) {
						keyValue = line.split("=");
						if (keyValue.length == 2) {
							queue.put(keyValue[0].trim(), keyValue[1].trim());
						}
						line = in.readLine();
					}

					// do something
					if (queue.get("queue_type").equalsIgnoreCase("Execution")
							&& queue.get("started").equalsIgnoreCase("True")
							&& queue.get("enabled").equalsIgnoreCase("True")
							&& (queue.get("acl_users") == null
									|| queue.get("acl_users").contains("*") || queue
									.get("acl_users").contains("DCA"))) {
						int priority = 0;
						if (queue.get("Priority") != null) {
							priority = Integer.parseInt(queue.get("Priority"));
						}
						pbs.add(new PbsQueue(queue.get("Queue"), priority));
					}
					queue.clear();
				} else {
					line = in.readLine();
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		queueSort = new String[pbs.size()];
		for(int i=0; i<queueSort.length; i++) {
			queueSort[i] = pbs.remove().getName();
		}

	}

	public static void main(String[] args) {
		PbsQueueInfo p = new PbsQueueInfo();
		String[] q = p.getQueueSort();
		for(String i : q) {
			System.out.println(i);
		}
	}

}
