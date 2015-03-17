package sqlparser;
import java.util.ArrayList;

/**
 * A collection that allows easy printing of arrays
 * @author Mateusz Kaduk <mateusz.kaduk@gmail.com>
 *
 * @param <T>
 */
public class PrintableArrayList<T> extends ArrayList<T> {

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {

        int size = size();

        StringBuilder output = new StringBuilder();

        String delimiter = ",";

        for (int i = 0; i < size; i++) {

            output.append( get( i ) );
            if ( i < size - 1 ) {

                output.append( delimiter );
            }
        }

        return output.toString();
    }
}