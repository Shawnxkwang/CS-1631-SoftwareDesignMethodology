import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.NoSuchElementException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class NewTranslator {

	private String inputDir = "../ControllerComponents/", outputDir = "../",
			name = "aControllerComponent", pnmlPath;

	/**
	 * Constructor
	 * 
	 * @param n
	 *            name of the Controller component to be generated, can be different
	 *            from what's declared in pnml.xml, the generated Controller
	 *            component will prefer settings (more specifically, the name)
	 *            from pnml.xml
	 * @param in
	 *            input directory, containing pnml.xml, and possibly
	 *            initial.java, helper.java, and all number-ish.java
	 *            representing
	 *            handling logic code snippet of all messages
	 * @param out
	 *            output directory, directory for all generated files for that
	 *            Controller component, Create[NAME OF Controller COMPONENT].java,
	 *            Util.java
	 */
	public NewTranslator(String n, String in, String out) {
		// TODO Auto-generated constructor stub

		if (n != null && !n.equals("")) {
			name = n;
		}

		if (in != null && !in.equals("")) {

			in.replaceAll("\\+", "/");
			int len = in.length();
			String extension = in.substring(len - 4);

			if (extension.toLowerCase().equals(".xml")) {

				int forwardSlashPos = in.lastIndexOf('/');
				if (forwardSlashPos == -1) {
					this.inputDir = "";
				} else {
					this.inputDir = in.substring(0, forwardSlashPos + 1);
				}
				this.pnmlPath = in;

			} else {

				if (!in.endsWith("/")) {
					in = in + "/";
				}

				this.inputDir = in;
				this.pnmlPath = this.inputDir + "pnml.xml";
			}

		} else {
			/*
			 * if no input directory given, then
			 * ControllerComponents/[NAME OF Controller COMPONENT]/
			 * should be the default path
			 */
			this.inputDir += name + "/";
			this.pnmlPath = this.inputDir + "pnml.xml";
		}

		if (out != null && !out.equals("")) {
			out.replaceAll("\\+", "/");
			if (!out.endsWith("/")) {
				out = out + "/";
			}
			this.outputDir = out;
		}

		this.run();
	}

	/**
	 * Extract Pnml object from file
	 * 
	 * @param file
	 *            file contains pnml info
	 * @return
	 * 		Pnml object
	 */
	public Pnml extractToPnml(File file) {
		Pnml pnml = null;
		try {
			JAXBContext context = JAXBContext.newInstance(Pnml.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			pnml = (Pnml) unmarshaller.unmarshal(file);
			return pnml;

		} catch (JAXBException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return pnml;
	}

	private void generateUtil() {
		Path source = FileSystems.getDefault().getPath("template/Util.s");
		Path target = FileSystems.getDefault()
				.getPath(outputDir + "Components/" + name + "/Util.java");
		Path dir = FileSystems.getDefault()
				.getPath(outputDir + "Components/" + name + "/");
		try {
			Files.createDirectories(dir);
			Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void generateIndiScript() {
		Path source = FileSystems.getDefault()
				.getPath("template/controllerComponent_individualScript.s");
		Path target = FileSystems.getDefault().getPath(
				outputDir + "Scripts/runIndividualComp/run" + name + ".bat");

		try {
			BufferedReader reader = Files.newBufferedReader(source,
					StandardCharsets.UTF_8);
			String result = reader.lines()
					.map(x -> x.replaceAll("!name!", name))
					.reduce((x, y) -> (x + "\n" + y)).get();
			reader.close();

			writeContentTo(target, result, StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.WRITE, StandardOpenOption.CREATE);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// private void appendBatchScript() {
	// Path source = FileSystems.getDefault()
	// .getPath("template/controllerComponent_batchScript.s");
	// Path batchSource = FileSystems.getDefault()
	// .getPath(outputDir + "Scripts/runAllComps.bat");
	// try {
	//
	// BufferedReader readerBatch = Files.newBufferedReader(batchSource,
	// StandardCharsets.UTF_8);
	// List<String> list = readerBatch.lines()
	// .collect(Collectors.toList());
	//
	// BufferedReader reader = Files.newBufferedReader(source,
	// StandardCharsets.UTF_8);
	// reader.lines().map(x -> x.replaceAll("!name!", name)).forEach(x -> {
	// if (!list.contains(x)) {
	// list.add(x);
	// }
	// });
	// reader.close();
	//
	// writeContentTo(batchSource,
	// list.stream().reduce((x, y) -> (x + "\n" + y)).get(),
	// StandardOpenOption.TRUNCATE_EXISTING,
	// StandardOpenOption.WRITE, StandardOpenOption.CREATE);
	//
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	
	// private void generateRegXml(Pnml pnml) {
	//
	// List<Item> items = new ArrayList<>();
	// items.add(new Item("Name", name));
	// List<String> inputMessages = pnml.getTransitions().stream()
	// .filter(x -> x.getOrientation().getValue().equals("1"))
	// .map(x -> x.getId()).sorted().collect(Collectors.toList());
	// List<String> outputMessages = pnml.getTransitions().stream()
	// .filter(x -> x.getOrientation().getValue().equals("0"))
	// .map(x -> x.getId()).sorted().collect(Collectors.toList());
	// int inputMessageCounter = 1, outputMessageCounter = 1;
	// for (String i : inputMessages) {
	// items.add(new Item("InputMessage" + inputMessageCounter++, i));
	// }
	// for (String i : outputMessages) {
	// items.add(new Item("OutputMessage" + outputMessageCounter++, i));
	// }
	// Body body = new Body();
	// body.setItems(items);
	// Head head = new Head();
	// head.setMsgId("20");
	// head.setDescription("Create " + name);
	// Msg msg = new Msg();
	// msg.setHead(head);
	// msg.setBody(body);
	//
	// try {
	// JAXBContext context = JAXBContext.newInstance(Msg.class);
	// Marshaller marshaller = context.createMarshaller();
	// marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	// Path target = FileSystems.getDefault().getPath(
	// outputDir + "xml/InitXML/Create" + name + ".xml");
	//
	// marshaller.marshal(msg, Files.newBufferedWriter(target,
	// StandardCharsets.UTF_8, StandardOpenOption.CREATE,
	// StandardOpenOption.TRUNCATE_EXISTING));
	//
	// } catch (JAXBException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	class Caser implements Consumer<Entry<String, String>> {
		private StringBuilder builder;

		public Caser() {
			builder = new StringBuilder();
		}

		public String result() {
			return builder.toString();
		}

		public void combine(Caser other) {
			builder.append(other.builder.toString());
		}

		@Override
		public void accept(Entry<String, String> t) {
			// TODO Auto-generated method stub
			builder.append("\n\t\tcase " + t.getKey() + ":\n\t\t\t");
			builder.append(t.getValue() + "\n\t\t\tbreak;\n");
		}
	}

	class NewCaser implements Consumer<Entry<String, List<Transition>>> {
		private StringBuilder builder;

		public NewCaser() {
			builder = new StringBuilder();
		}

		public String result() {
			return builder.toString();
		}

		public void combine(NewCaser other) {
			builder.append(other.builder.toString());
		}

		@Override
		public void accept(Entry<String, List<Transition>> t) {
			// TODO Auto-generated method stub
			builder.append("case \"" + t.getKey() + "\":\n");
			builder.append("\t\t\t\tswitch (purpose) {\n");
			List<Transition> list = t.getValue();
			for (Transition tran : list) {
				builder.append("\t\t\t\tcase \"" + tran.getPurpose().getValue()
						+ "\":\n");
				builder.append("\t\t\t\t\tSystem.out.println(\""
						+ tran.getName().getValue()
						+ " received, start processing...\");\n");
				builder.append("\t\t\t\t\t" + tran.getCode().getValue() + "\n");
				builder.append("\n\t\t\t\t\tbreak;\n");
				builder.append("\t\t\t\t}\n");
			}

			builder.append("\t\t\t\tbreak;\n\t\t\t");
		}

	}

	private void generateComponent(Pnml pnml) {
		Path source = FileSystems.getDefault()
				.getPath("template/componentTemplate.s");
		Path target = FileSystems.getDefault().getPath(
				outputDir + "Components/" + name + "/Create" + name + ".java");

		Place p0 = pnml.getPlaces().get(0);

		String initialPath = p0.getInitialCode().getValue();
		String helperPath = p0.getHelperCode().getValue();
		String helperClassPath = p0.getHelperClassCode().getValue();
		String scope = p0.getScope().getValue();

		if (initialPath == null || initialPath.equals("")) {
			initialPath = "initial.java";
		}

		if (helperPath == null || helperPath.equals("")) {
			helperPath = "helper.java";
		}
		
		if (helperClassPath == null || helperClassPath.equals("")) {
			helperClassPath = "helperClass.java";
		}

		String initialContent = readContentFromWithTabs(
				FileSystems.getDefault().getPath(inputDir + initialPath), 1);
		String helperContent = readContentFromWithTabs(
				FileSystems.getDefault().getPath(inputDir + helperPath), 1);
		String helperClassContent = readContentFromWithTabs(
				FileSystems.getDefault().getPath(inputDir + helperClassPath), 0);
		pnml.getPlaces().remove(0);

		List<String> senders = pnml.getPlaces().stream()
				.map(x -> x.getName().getValue()).collect(Collectors.toList());

		Map<String, List<Transition>> senderToPurposes = senders.stream()
				.collect(Collectors.toMap(String::toString, v -> {
					return new ArrayList<Transition>();
				}));

		String incomingMessages = pnml.getTransitions().stream().map(x -> {
			return x.getPurpose().getValue();
		}).collect(Collectors.joining("|Alert:", "|Alert:", ""));

		String casesString = senderToPurposes.entrySet().stream().map(en -> {
			String sender = en.getKey();
			List<Transition> trans = pnml.getTransitions().stream()
					.filter(tran -> tran.getSource().getValue().equals(sender))
					.collect(Collectors.toList());
			en.setValue(trans);
			return en;
		}).map(en -> {
			List<Transition> trans;
			trans = en.getValue().stream().map(t -> {
				Path path;
				String purpose = t.getPurpose().getValue();
				String code = t.getCode().getValue();
				if (code == null || code.equals("")) {
					path = FileSystems.getDefault()
							.getPath(inputDir + purpose + ".java");
				} else {
					path = FileSystems.getDefault().getPath(code);
				}

				t.getCode().setValue(readContentFromWithTabs(path, 5));
				return t;
			}).collect(Collectors.toList());

			en.setValue(trans);
			return en;
		}).collect(NewCaser::new, NewCaser::accept, NewCaser::combine).result();

		//
		// Map<String, String> pathMap = pnml
		// .getTransitions()
		// .stream()
		// .filter(x -> x.getOrientation().getValue().equals("1"))
		// .collect(
		// Collectors.toMap(Transition::getId, t -> t.getCode()
		// .getValue()));
		//
		// String casesString = pathMap
		// .entrySet()
		// .stream()
		// .map(x -> {
		// String msgId = x.getKey();
		// String pathString = x.getValue();
		// Path path;
		// if (pathString == null || pathString.equals("")) {
		// path = FileSystems.getDefault().getPath(
		// inputDir + msgId + ".java");
		// } else {
		// path = FileSystems.getDefault().getPath(pathString);
		// }
		// try {
		// x.setValue(readContentFromWithTabs(path, 3));
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// return x;
		// }).collect(Caser::new, Caser::accept, Caser::combine).result();
		//
		try {
			BufferedReader reader = Files.newBufferedReader(source,
					StandardCharsets.UTF_8);
			String finalResult = reader.lines()
					.reduce(new StringBuilder(), (x, y) -> {
						String newY = y.replaceFirst("!name!", name)
								.replaceFirst("!scope!", scope)
								.replaceFirst("!initialCode!", initialContent)
								.replaceFirst("!senderCases!", casesString)
								.replaceFirst("!helperCode!", helperContent)
								.replaceFirst("!helperClassCode!", helperClassContent)
								.replaceFirst("!incomingMessages!", incomingMessages);
						x.append(newY + "\n");
						return x;
					} , (left, right) -> left.append(right.toString()))
					.toString();
			writeContentTo(target, finalResult, StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// private String readContentFrom(Path path) {
	// // Path path = FileSystems.getDefault().getPath(pathString);
	// try {
	// BufferedReader reader = Files.newBufferedReader(path,
	// StandardCharsets.UTF_8);
	// return reader.lines().reduce((x, y) -> (x + "\n" + y)).get();
	// } catch (NoSuchElementException e) {
	//
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return "";
	// }

	private String readContentFromWithTabs(Path path, int count) {
		// Path path = FileSystems.getDefault().getPath(pathString);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < count; i++) {
			builder.append("\t");
		}
		try {
			BufferedReader reader = Files.newBufferedReader(path,
					StandardCharsets.UTF_8);

			return reader.lines()
					.reduce((x, y) -> (x + "\n" + builder.toString() + y))
					.get();
		} catch (NoSuchElementException e) {

		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "";
		}
		return "";
	}

	private void writeContentTo(Path path, String content,
			OpenOption... options) {
		// Path path = FileSystems.getDefault().getPath(pathString);
		try {
			BufferedWriter writer = Files.newBufferedWriter(path,
					StandardCharsets.UTF_8, options);
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void run() {
		Pnml pnml = extractToPnml(new File(pnmlPath));
		generateUtil();
		generateIndiScript();
		// appendBatchScript();

		// generateRegXml(pnml);

		generateComponent(pnml);

		// JAXBContext context = JAXBContext.newInstance(Pnml.class);
		// Marshaller marshaller = context.createMarshaller();
		// marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		// marshaller.marshal(pnml, System.out);
	}

	// public static void main(String[] args) {
	// new NewTranslator("TempBloodPressure", "", "");
	// }
}
