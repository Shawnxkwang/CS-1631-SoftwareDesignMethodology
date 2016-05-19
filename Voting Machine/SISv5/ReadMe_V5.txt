
========================================================

	Setup & Run

========================================================

1. Prepare Running Environment
	a) Make sure you are using 

		** Windows OS ** 

		(NO OTHER OSs are officially supported, however feel free to try out stuff)

	and have properly installed 

		** JDK 1.8 **

	If you haven’t installed JDK 1.8, please follow the steps in this link: http://www.oracle.com/technetwork/java/javase/downloads/index.html
	b) For 32 Bit machines, install /Libraries/vcredist_x86_VS2012.exe & /Libraries/vcredist_x86_VS2013.exe; For 64 Bit machines, install /Libraries/vcredist_x64_VS2012.exe & /Libraries/vcredist_x64_VS2013.exe; 

	* Under Components, you'll see .jar and .dll
		- javax.mail.jar is for sending emails in Java
		- jtds-1.2.5.jar is for JDBC driver
		- RXTXcomm.jar/rxtcParallel.dll/rxtxSerial.dll are drivers for PaceTech machine
	  These are all embedded so no configuration needed, but copies can be found under Libraries.
	  NOTE that if you happen to use a 32 bit machine, swap in 32 bit RXTXcomm drivers under Libraries for Components. Just copy-paste.

2. Download Project
	Download the SISv5.zip from the course web. Unzip the file, and put it under any path you want.

*3. [DEV version] Prepare Sensors
	Before running the PersonalHealthcare2015 program, please plug in the sensors you may need.

	*3.1. Temperature sensor
		Plug in the temperature sensor to your computer's USB port.

	*3.2. PaceTech machine
		a) Turn on the health monitor;
		b) Connect the device to computer by RS232 cable or RS232 cable plus USB-RS232 adapter;
		c) Wait for a while until the monitor screen shows up.

	*3.3. Kinect sensor
		a) Be sure to place the sensor indoors.
		b) Plug in power adapter, then usb connector
		c) According to the image show on the kinect console screen, adjust the angle of the sensor so that the floor can be detected.
		d) For successful detection, be sure your head is within the detection scope.

4. Run

	4.1. Initialize:
		a). Go to SISv5/Scripts/: 
			i) Double click the 'runserver.bat' file to start the SIS server. 
			ii) Double click the 'runInitializer.bat' file to batch register all existing components (read all XML files under SISv5/xml/InitXML, including Controller components).
		b). GO to SISv5/Scripts/runIndividualComp/:
			i) Double click the 'runGUI.bat' file to start GUI. You will see a graphical window, which will display active components' information later. 

	4.2. Run components:

		4.2.1. Temperature Sensor:
			a) Go to SISv5/Scripts/runIndividualComp/, Double click the 'runTemp.bat' file
			b) Back to the GUI, now you can see a new component named 'Temp' here. 
			c) Configure the temperature component: 
				i) Change the value of Max and Min as the normal body temperature threshold you want to set. If you want to see the alert information later, you can set a low Max value or a high Min value, e.g. 27 and 20.
				ii) Change the Start Date and End Date to the duration you want the system to work (same date WON'T work). Normally, you can only change the End Data to the next day's date.
				iii) Change the refresh time to a reasonable time interval, like 1000 (ms).
				iv). Every time you change the settings, you need to deactivate and reactive the component. 

		*4.2.2. [DEV version] BloodPressure, SPO2, EKG Sensors (PaceTech machine):
			*a) Go to /../SISv5/Scripts/runIndividualComp/:
				*i) Double click the 'runInputProcessor.bat' to start the data preprocessor of the PaceTech machine.
				*ii) Double click the 'runBloodPressure.bat' and/or 'runEKG.bat' and/or 'runSPO2.bat'.
			*b) Back to the GUI, now you can see one 'InputProcessor' component and/or 'BloodPressure' and/or 'EKG' and/or 'SPO2' components.
			*c) Configure the components: 
				*i) For the 'InputProcessor', we only need to configure the date.
				*ii) For the 'BloodPressure', 'SPO2', 'EKG' components, see the reference in step 4.2.1.(c).
			*d) Press start/stop on PaceTech machine to collect any data.

		*4.2.3. (Only applicable for DEV version) Kinect:
			*a) Go to /../SISv5/Scripts/runIndividualComp/:
				*i) Double click the 'runKinectSensor.bat' to start the main data processor of the Kinect.
				*ii) Double click the 'runKinectMonitor.bat'.
			*b) Back to the GUI.
			*c) Configure the components: 
				See the reference in step 4.2.1.(c).

	4.3. Run Uploader
		a) Go to SISv5/Scripts/runIndividualComp/, double click the 'runUploader.bat'
		b) Go to the GUI. In the bottom of the window, you can enter into the email addresses you want to receive the alerts. Separate the email addresses by comma. Then click the change button. 

	4.4. Getting Alerts or Emergency Alerts
		a) Getting Alerts:
			The alerts are generated from basic components. When the values exceed the scope you set, it will generate the alerts, and update the data to database, and also send the alert emails.
		b) Getting Emergency Alerts:
			The alerts are generated from Controller components. By default, the system has already generated one Controller component combined 'Temperature' and 'BloodPressure'. You can run the 'runTempBloodPressure.bat' under the path /../SISv5/Scripts/runIndividualComp/. Once one the sub component's value exceed the scope, it will generate the emergency alerts. Also, you can generate the Controller components by your own. See the reference in step 4.5.

	4.5. Configure your own Controller component
		a) First write your Controller component configuration file in xml and prepare any program(s) needed. You can see the sample 'CreateTempBloodPressure.xml' under the path SISv5/xml/InitXML and many other configuration files under SISv5/ControllerComponents/TempBloodPressure. 
			i) 'CreateTempBloodPressure.xml' is the message for registration of that Controller component. 'InputPath' is the path to Controller component config files folder, leave empty to reference /SISv5/ControllerComponents, 'OutputPath' is where the generated Controller component would be, leave empty to reference SISv5/Components
			ii) Write pnml.xml, consider 'place' certain component, config its 'name', 'scope', 'initialCode', 'helperCode', 'helperClassCode'. Leave values for those codes empty to reference default files named 'intial.java', 'helper.java', 'helperClass.java' under the Controller component config files folder, of course you should provide these files yourself. You should put code snippets in these files.
			iii) Consider 'transition' a Alert message, give it a name, define its 'source' component and 'purpose'. A 'source' component can send Alert messages with different 'purposes'. Leave 'code' empty to reference 'PURPOSE_VALUE.java' under the Controller component config files folder.
			iv) No perticular order for 'transitions', but the first 'place'

				** MUST **

			be the Controller component itself.
			iv) For how these snippets are used, check SISv5/NewSISServer/template/componentTemplate.s, open it with any text editor. Look for !XXX! in the template.

		b) Corresponding scripts for execution will be generated under /../SISv5/Scripts, corresponding Controller component will be generated under /../Components
		* You can manually register and generate a Controller component, go to /../SISv5/, double click the PrjRemote.jar to register the new Controller component. See reference below.

========================================================

	Additional Tool 'PrjRemote'

========================================================

		For unimplemented components, you can mock messages that are supposed to be sent by them using PrjRemote.

		A sample of textual representation of a message:

		<?xml version="1.0" standalone="yes"?>
		<!--Generated by SISProjectCreator Version 1.0 -->
		<Msg>
			<Item>
				<Key>Scope</Key>
				<Value>SIS.Scope1</Value>
			</Item>
			<Item>
				<Key>MessageType</Key>
				<Value>Register</Value>
			</Item>
			<Item>
				<Key>Role</Key>
				<Value>Basic</Value>
			</Item>
			<Item>
				<Key>Name</Key>
				<Value>BloodPressure</Value>
			</Item>
		</Msg>

		As you see from above, a registration message is written as XML format.

		There are 4 Items: Scope, MessageType, Role & Name.

		Each Item is a Key-Value pair.

		Change coresspondingly in your own message.

		You can add as many Items as you like.

		Save your XML file to /xml/DataXML (for other messages) or /xml/InitXML (for registration messages)

		You can manually create a batch file (.txt) to group messages together.

		You'd better use relative paths in your batch file like shown in ScriptSample.txt under xml/DataXML, the starting point of any path should be root of this project, you can imagine finding one file as a human being named "PrjRemote.jar".

		There are some wrong usages in ScriptSample.txt, if you notice certain messages won't load, check these and you'll usually find out the reason.

		The batch file is merely instruction for PrjRemote.jar. Thus the location of the batch file (.txt) doesn't matter, but PrjRemote.jar's location does, since it'll affect the relative paths.

		---------------------------------------------------------------

		This tool helps user to register one or more selected components. 
		a) Go to /../SISv5/, double click the PrjRemote.jar
		b) Expand 'Connection & Scope' to configure
			* IP, Port to the SIS Server
			* Refresh rate to update the 'Message Received' table
			* Scope, change to inject into different subsystem	
		c) Click 'Connect', once connected, expand 'Message(s)' to 'Load' single message (.xml) or many message(s) by batch script (.txt), and 'Send' messages.

========================================================

	Message Types

========================================================

All messages are formed by Key-Value pairs.

1. Reading: a normal reading from a Basic component

	Scope 					The operating level of this message
	MessageType				"Reading"
	Sender					Which component sends this
	[Receiver]				Optional, which component receives this
	YOUR_OWN_ITEM_1			
	...
	YOUR_OWN_ITEM_N
	Date 					Date when this message is sent

2. Alert: an alert from a Basic component

	Scope 					The operating level of this message
	MessageType				"Alert"
	Sender					Which component sends this
	[Receiver]				Optional, which component receives this
	YOUR_OWN_ITEM_1			
	...
	YOUR_OWN_ITEM_N
	Date 					Date when this message is sent

3. Setting: a setting command from a Monitor

	Scope 					The operating level of this message
	MessageType				"Setting"
	Sender					Which component sends this
	[Receiver]				Optional, which component receives this
	YOUR_OWN_ITEM_1			
	...
	YOUR_OWN_ITEM_N
	Purpose 				The purpose of this setting action,
							preferably no spaces, used for 
							differentiate settings for a component

4. Register: a registration request

	Scope 					The operating level of this message
	MessageType				"Register"
	Role					Basic/Controller/Monitor/Advertiser/Debugger
							/YOUR_OWN_ROLE

							* Unless behaviors added to SISServer
							otherwise using your own customized
							role here won't do anything. See
							below for references.

	Name 					Name of the component, no duplicates
							allowed for one Scope
	[InputPath]				Optional, for Controller components only,
							provide the location of all required
							sources to build a Controller component

							* See below for references.

	[OutputPath]			Optional, for Controller components only,
							provide the location to put the generated Controller component

							* See below for references.

5. Confirm: a confirmation from SISServer

	Scope 					The operating level of this message
	MessageType				"Confirm"
	Sender					Which component sends this
	Receiver				which component receives this

6. Connect: a connecting request from a component other than Monitors

	Scope 					The operating level of this message
	MessageType				"Connect"
	Role					Basic/Controller/Monitor/Advertiser/Debugger
							/YOUR_OWN_ROLE

							* Unless behaviors added to SISServer
							otherwise using your own customized
							role here won't do anything. See
							below for references.
	Name 					Name of the component, no duplicates
							allowed for one Scope

7. Emergency: a emergency message from a Controller component

	Scope 					The operating level of this message
	MessageType				"Emergency"
	Sender					Which component sends this
	MainComponent			The Basic component initiates
							the emergency
	HelperComponents		All other Basic components which
							provide useful information
	Note 					Additional Note
	Date 					Date when this message is sent

========================================================

	Roles

========================================================

There are 5 pre-defined Roles in SISv5, Basic/Controller/Monitor/Advertiser/Debugger

Basic component is your average, run-of-the-mill guy who collects data and occasionally send out Alert.

Controller component can be responsible for processing Reading/Alert from Basic components, and occasionallly send out Emergency.

Monitor component watches over Reading/Alert all across a Scope.

Advertiser component spots Alert/Emergency and tell the outside world about it.

Debugger component, like PrjRemote, will see EVERY MESSAGE within a Scope.

You can, however, add new Roles to the system, head over to SISServer.java/SISTask.java under NewSISServer and modify related parts. You need to have knowledge about how Stream in Java 8 works. Google "Java 8 Stream" and go through tutorials on the first page, that should do it.