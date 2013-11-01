package com.arvue.apps.easidemo;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.Component;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Title;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Image;
import com.vaadin.ui.themes.Reindeer;

@PreserveOnRefresh
@SuppressWarnings("serial")
public class App extends UI {

        /* User interface components are stored in session. */
        private Table photoList = new Table();
        // private TextField searchField = new TextField();
        private Button addNewContactButton = new Button("New");
        private Label amountLabel = new Label();
        private Label wellcomeLabel = new Label("CONTENT_PREFORMATTED");
        private Button removeContactButton = new Button("Remove");
        private FormLayout editorLayout = new FormLayout();
        private FieldGroup editorFields = new FieldGroup();
        private Embedded image = null;
        private TabSheet tabsheet = new TabSheet();

        private static final String FNAME = "Name";
        private static final String LNAME = "URI";
        
        private static final String[] fieldNames = new String[] { FNAME, LNAME };

        /*
         * Any component can be bound to an external data source. This example uses
         * just a dummy in-memory list, but there are many more practical
         * implementations.
         */
        IndexedContainer contactContainer = createDummyDatasource();
        HorizontalLayout imageLayout;
        VerticalLayout startTab;
        VerticalLayout manageTab;
        VerticalLayout galleryTab;
        VerticalLayout billingTab;
        /*
         * After UI class is created, init() is executed. You should build and wire
         * up your user interface here.
         */
        protected void init(VaadinRequest request) {
                initTabSheet(); 
                initLayout();
                initContactList();
                initEditor();
                initAddRemoveButtons();
                initImages();
                setBillingInfo();
        }
        
        private void initTabSheet() {
            
            wellcomeLabel=new Label ("   Wellcome to EASI-Demo Photo Gallery\n \n"+
               "   You can manage and view your photos. Chargeing information is available on Billing page.",
               Label.CONTENT_PREFORMATTED);
               
            startTab = new VerticalLayout();
            startTab.setCaption("EASI-Demo");
            startTab.addComponent(wellcomeLabel);
            tabsheet.addTab(startTab);
            manageTab = new VerticalLayout();
            manageTab.setCaption("Management");
            tabsheet.addTab(manageTab);
            galleryTab = new VerticalLayout();
            galleryTab.setCaption("Gallery");
            tabsheet.addTab(galleryTab);
            billingTab = new VerticalLayout();
            billingTab.setCaption("Billing");
            tabsheet.addTab(billingTab);
        
        }
            private void setBillingInfo() {
                int amount = contactContainer.size();
                amountLabel.setCaption("Number of photos: " + amount);
            }


        /*
         * In this example layouts are programmed in Java. You may choose use a
         * visual editor, CSS or HTML templates for layout instead.
         */
        private void initLayout() {

                /* Root of the user interface component tree is set */
                HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
                
                //setContent(splitPanel);
                setContent(tabsheet);

                /* Build the component tree */
                VerticalLayout leftLayout = new VerticalLayout();
                splitPanel.addComponent(leftLayout);
                leftLayout.addComponent(photoList);
                
                HorizontalLayout bottomLeftLayout = new HorizontalLayout();
                leftLayout.addComponent(bottomLeftLayout);
                leftLayout.addComponent(editorLayout);
                bottomLeftLayout.addComponent(addNewContactButton);
               
                
                billingTab.addComponent(amountLabel);
                
                manageTab.addComponent(splitPanel);
                manageTab.addComponent(leftLayout);
                manageTab.addComponent(bottomLeftLayout);
                
              //  amountLabel.addStyleName(Reindeer.LABEL_H1);
                imageLayout = new HorizontalLayout();
                galleryTab.addComponent(imageLayout);

                /* Set the contents in the left of the split panel to use all the space */
                leftLayout.setSizeFull();

                /*
                 * On the left side, expand the size of the contactList so that it uses
                 * all the space left after from bottomLeftLayout
                 */
                leftLayout.setExpandRatio(photoList, 1);
                photoList.setSizeFull();

                /*
                 * In the bottomLeftLayout, searchField takes all the width there is
                 * after adding addNewContactButton. The height of the layout is defined
                 * by the tallest component.
                 */
                bottomLeftLayout.setWidth("50%");
           
                /* Put a little margin around the fields in the right side editor */
                editorLayout.setMargin(true);
                editorLayout.setVisible(false);
        }

        private void initEditor() {

                editorLayout.addComponent(removeContactButton);

                /* User interface can be created dynamically to reflect underlying data. */
                for (String fieldName : fieldNames) {
                        TextField field = new TextField(fieldName);
                        editorLayout.addComponent(field);
                        field.setWidth("100%");

                        /*
                         * We use a FieldGroup to connect multiple components to a data
                         * source at once.
                         */
                        editorFields.bind(field, fieldName);
                }

                /*
                 * Data can be buffered in the user interface. When doing so, commit()
                 * writes the changes to the data source. Here we choose to write the
                 * changes automatically without calling commit().
                 */
                editorFields.setBuffered(false);
        }

       

    

        private void initAddRemoveButtons() {
                addNewContactButton.addClickListener(new ClickListener() {
                        public void buttonClick(ClickEvent event) {

                                /*
                                 * Rows in the Container data model are called Item. Here we add
                                 * a new row in the beginning of the list.
                                 */
                                contactContainer.removeAllContainerFilters();
                                Object contactId = contactContainer.addItemAt(0);

                                /*
                                 * Each Item has a set of Properties that hold values. Here we
                                 * set a couple of those.
                                 */
                                photoList.getContainerProperty(contactId, FNAME).setValue(
                                                "Name");
                                photoList.getContainerProperty(contactId, LNAME).setValue(
                                                "URI");

                                /* Lets choose the newly created contact to edit it. */
                                photoList.select(contactId);
                        
                        }
                        
                });

                removeContactButton.addClickListener(new ClickListener() {
                        public void buttonClick(ClickEvent event) {
                                Object contactId = photoList.getValue();
                                photoList.removeItem(contactId);
                                initImages();
                                setBillingInfo();
                        }
                });
        }

        private void initContactList() {
                photoList.setContainerDataSource(contactContainer);
                photoList.setVisibleColumns(new String[] { FNAME, LNAME});
                photoList.setSelectable(true);
                photoList.setImmediate(true);

                photoList.addValueChangeListener(new Property.ValueChangeListener() {
                        public void valueChange(ValueChangeEvent event) {
                                Object contactId = photoList.getValue();

                                /*
                                 * When a contact is selected from the list, we want to show
                                 * that in our editor on the right. This is nicely done by the
                                 * FieldGroup that binds all the fields to the corresponding
                                 * Properties in our contact at once.
                                 */
                                if (contactId != null)
                                        editorFields.setItemDataSource(photoList
                                                        .getItem(contactId));
                                
                                editorLayout.setVisible(contactId != null);
                                initImages();
                                setBillingInfo();
                        }
                });
        }

        /*
         * Generate some in-memory example data to play with. In a real application
         * we could be using SQLContainer, JPAContainer or some other to persist the
         * data.
         */
        private static IndexedContainer createDummyDatasource() {
                IndexedContainer ic = new IndexedContainer();

                for (String p : fieldNames) {
                        ic.addContainerProperty(p, String.class, "");
                }

                /* Create dummy data by randomly combining first and last names */
                String[] fnames = { "Bike", "Dog", "Aatos"  };
                String[] lnames = { "http://www.cs.tut.fi/~ruokonea/canyon_wxc_6.0_2008.jpg", "http://www.cs.tut.fi/~ruokonea/aatos.jpg", "http://www.cs.tut.fi/~ruokonea/aatos2.png"};
                
                for (int i = 0; i < 3; i++) {
                        Object id = ic.addItem();
                        ic.getContainerProperty(id, FNAME).setValue(
                                        fnames[i]);
                        ic.getContainerProperty(id, LNAME).setValue(
                                        lnames[i]);
                }

                return ic;
        }
        
        private void initImages(){
            imageLayout.removeAllComponents();
            for (Object itemId: contactContainer.getItemIds()) {
        
            Item item = contactContainer.getItem(itemId);
            String name= (String)item.getItemProperty(FNAME).getValue();
            String uri=  (String)item.getItemProperty(LNAME).getValue();
            image = new Embedded(name, new ExternalResource(uri));
            imageLayout.addComponent(image);
            }
     
        }
}