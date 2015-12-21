    /**
     * Adds  a new DOL descriptor instance to the descriptor instance associated with 
     * this XMLNode
     *
     * @param newDescriptor the new descriptor
     */    
    public void addDescriptor(Object  newDescriptor) {
        Logger logger = DOLUtils.getDefaultLogger();
        if (newDescriptor instanceof EjbReference) {            
            descriptor.addEjbReferenceDescriptor(
                        (EjbReference) newDescriptor);
        } else  if (newDescriptor instanceof EnvironmentProperty) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Adding env entry" + newDescriptor);
            }
            descriptor.addEnvironmentProperty((EnvironmentProperty) newDescriptor);
        } else if (newDescriptor instanceof WebComponentDescriptor) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Adding web component" + newDescriptor);
            }
            descriptor.addWebComponentDescriptor((WebComponentDescriptor) newDescriptor);
        } else if (newDescriptor instanceof TagLibConfigurationDescriptor) {
            // for backward compatibility with 2.2 and 2.3 specs, we need to be able 
            // to read tag lib under web-app. Starting with 2.4, the tag moved under jsp-config
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Adding taglib component " + newDescriptor);
            }
            if (descriptor.getJspConfigDescriptor()==null) {
                descriptor.setJspConfigDescriptor(new JspConfigDescriptorImpl());
            }
            descriptor.getJspConfigDescriptor().addTagLib((TagLibConfigurationDescriptor) newDescriptor);
        } else if (newDescriptor instanceof JspConfigDescriptorImpl) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Adding JSP Config Descriptor" + newDescriptor);
            }
            if (descriptor.getJspConfigDescriptor()!=null) {
                throw new RuntimeException(
                    "Has more than one jsp-config element!");
            }
            descriptor.setJspConfigDescriptor(
                (JspConfigDescriptorImpl)newDescriptor);
        } else if (newDescriptor instanceof LoginConfiguration) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Adding Login Config Descriptor" + newDescriptor);
            }
            if (descriptor.getLoginConfiguration()!=null) {
                throw new RuntimeException(
                    "Has more than one login-config element!");
            }
            descriptor.setLoginConfiguration(
                (LoginConfiguration)newDescriptor);
        } else if (newDescriptor instanceof SessionConfig) {
            if (descriptor.getSessionConfig() != null) {
                throw new RuntimeException(
                    "Has more than one session-config element!");
            }
            descriptor.setSessionConfig((SessionConfig)newDescriptor);
        } else {
            super.addDescriptor(newDescriptor);
        }
    }   