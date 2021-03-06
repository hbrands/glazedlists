/* Glazed Lists                                                 (c) 2003-2013 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/
package ca.odell.glazedlists.impl.swing;

import ca.odell.glazedlists.swing.TableModelEventAdapter;
import ca.odell.glazedlists.swing.TableModelEventAdapter.Factory;

import javax.swing.table.AbstractTableModel;

/**
 * A factory for creating a {@link DefaultTableModelEventAdapter}.
 *
 * @param <E> list element type
 *
 * @author Holger Brands
 */
public class DefaultTableModelEventAdapterFactory<E> implements Factory<E> {
    /** Singleton instance of DefaultTableModelEventAdapterFactory. */
    private static final Factory INSTANCE = new DefaultTableModelEventAdapterFactory();

    /**
     * {@inheritDoc}
     */
    @Override
    public TableModelEventAdapter<E> create(AbstractTableModel tableModel) {
        return new DefaultTableModelEventAdapter<E>(tableModel);
    }

    /**
     * Gets the factory instance singleton.
     *
     * @return the factory instance singleton
     */
    @SuppressWarnings("unchecked")
    public static <E> Factory<E> getInstance() {
        return INSTANCE;
    }
}