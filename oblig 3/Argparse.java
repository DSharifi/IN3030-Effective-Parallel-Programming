import java.util.HashMap;
import java.util.LinkedList;

public class Argparse {

    private String[] args;
    private HashMap<String, Object> map = new HashMap<String, Object>();;
    private LinkedList<Argument> arguments = new LinkedList<Argument>();
    private Boolean optionalSet = false;

    public Argparse(String[] args){
        this.args = args;
    }

    private String usage(){
        StringBuilder usage = new StringBuilder();
        usage.append("Arguments:\n");
        for (Argument argument : arguments) {
            usage.append(argument.name +
                        " (" + argument.type.toString() +
                        "): " +
                        (argument.required ? "REQUIRED":"OPTIONAL") +
                        "\n");
        }

        return usage.toString();
    }

    public Boolean parse(){
        int counter = 0;
        try {
            for (Argument argument : this.arguments) {
                if(!argument.required && args.length <= counter){
                    map.put(argument.name, null);
                    counter++;
                    continue;
                }

                switch(argument.type){
                    case STRING:
                        map.put(argument.name, args[counter++]);
                        break;

                    case INTEGER:
                        map.put(argument.name, Integer.parseInt(args[counter++]));
                        break;

                    case DOUBLE:
                        map.put(argument.name, Double.parseDouble(args[counter++]));
                        break;

                    case FLOAT:
                        map.put(argument.name, Float.parseFloat(args[counter++]));
                        break;

                    case LONG:
                        map.put(argument.name, Long.parseLong(args[counter++]));
                        break;

                    case BOOLEAN:
                        map.put(argument.name, Boolean.parseBoolean(args[counter++]));
                        break;

                    default:
                        throw new RuntimeException("Use correct enum type");
                }
            }
        } catch (Exception e) {
            System.err.println(e);
            System.out.println(usage());
            return false;
        }

        if(counter < args.length){
            System.err.printf("Too many arguments, expected %d got %d\n", counter, args.length);
            System.out.println(usage());
            return false;
        }

        return true;
    }

    public void add(Argument argument){
        if(optionalSet && argument.required)
            throw new RuntimeException("Can't add required arguments after optional");
        arguments.add(argument);
        this.optionalSet = !(argument.required);
    }

    public Object get(String key){
        try {
            return map.get(key);
        } catch (Exception e) {
            return null;
        }
    }

}

enum Type{
    STRING,
    INTEGER,
    DOUBLE,
    FLOAT,
    LONG,
    BOOLEAN
}

class Argument{
    public String name;
    public Type type;
    public Boolean required;
    
    public Argument(String name, Type type, Boolean required){
        this.name = name;
        this.type = type;
        this.required = required;
    }
}