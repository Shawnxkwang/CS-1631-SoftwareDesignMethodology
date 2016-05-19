// The following function is used to calculate the slope of temperature
public static double calculateSlope() throws IOException
{
    String fileName = "temperatureRecord.csv";
    BufferedReader br  = new BufferedReader(new FileReader(fileName));

    double tempSlope = 0.0;

    String temp;
    while((temp = br.readLine()) != null)
    {
        String[] str = temp.split(",");
        if(tempRecord.size() <= 20)
        {
            double newEntry = Double.parseDouble(str[1]);
            tempRecord.add(newEntry);
        }
        else
        {
            tempRecord.remove(0);
            double newEntry = Double.parseDouble(str[1]);
            tempRecord.add(newEntry);
        }
    }
    System.out.println("");
    System.out.print("The twenty minute temperature record are as follows: ");
    System.out.println(tempRecord);
    System.out.println("");
    Collections.sort(tempRecord);
    double min = tempRecord.get(0);
    double max = tempRecord.get(tempRecord.size() - 1);
    tempSlope = (max - min) / max;
    System.out.printf("tempSlope is %.2f", tempSlope);
    System.out.println("\n");

    return tempSlope;
}