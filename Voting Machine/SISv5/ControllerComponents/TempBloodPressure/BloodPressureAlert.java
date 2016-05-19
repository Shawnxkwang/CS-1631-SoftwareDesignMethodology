System.out.println("Start to check the Slope of Temperature...\n");
double tempSlopeAboutBP = calculateSlope();
String alertMsgAboutBP = "Complex Alert!";

String systString = kvList.getValue("Systolic");
String diasString = kvList.getValue("Diastolic");
int syst = 0, dias = 0;
if (systString != null && !systString.equals(""))
{
    try
    {
        syst = Integer.parseInt(systString);
    }
    catch(Exception e)
    {
        syst = 0;
    }
}

if (diasString != null && !diasString.equals(""))
{
    try
    {
        dias = Integer.parseInt(diasString);
    }
    catch(Exception e)
    {
        dias = 0;
    }
}
// 140/90, 120/80 this is for demo purposes
if(syst > 100 || dias > 70)
{
    if(tempSlopeAboutBP > 0.2)
    {
        alertMsgAboutBP = "The Patient's Blood Pressure is too high, it is possible because of the high rate of temperature increasing! please check the temperature!!!";
    }
    if(tempSlopeAboutBP < -0.2)
    {
        alertMsgAboutBP = "The Patient's Blood Pressure is too high. it is possible because of the high rate of temperature decreasing! please check the temperature!!! ";
    }
}
else if(syst < 70 || dias < 50)
{
    if(tempSlopeAboutBP > 0.2)
    {
        alertMsgAboutBP = "The Patient's Blood Pressure is too low, it is possible because of the high rate of temperature increasing! please check the temperature!!!";
    }
    if(tempSlopeAboutBP < -0.2)
    {
        alertMsgAboutBP = "The Patient's Blood Pressure is too low. it is possible because of the high rate of temperature decreasing! please check the temperature!!! ";
    }
}

if(tempSlopeAboutBP > 0.2 || tempSlopeAboutBP < -0.2)
{
    System.out.println("========= Send out Emergency message =========");

    emergency.putPair("MainComponent", "BloodPressure");
    emergency.putPair("AuxComponents", "Temp");
    emergency.putPair("Note", alertMsgAboutBP);
    emergency.putPair("Date", System.currentTimeMillis() + "");

    encoder.sendMsg(emergency);
}