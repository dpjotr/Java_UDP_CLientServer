package UDP;

import java.io.*;

public class Services {
    static public Object convertObjectFromBytes(byte[] bytes)  {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(bis)) {
            return  in.readObject();
        } catch (Exception e) {
            System.err.println("Error convertMessageFromBytes: " + e.getMessage());
            return null;
        }
    }

    static public byte[] convertObjectToBytes(Object obj)  {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(obj);
            return bos.toByteArray();
        }
        catch (Exception e){
            System.out.println("Error convertObjectToBytes: " + e.getMessage());
        }
        return  null;
    }


}
