    /** 
     * receives notification of the end of an XML element by the Parser
     * 
     * @param element the xml tag identification
     * @return true if this node is done processing the XML sub tree
     */
    public boolean endElement(XMLElement element) {
        if (WebTagNames.DISTRIBUTABLE.equals(element.getQName())) {       
            descriptor.setDistributable(true);
            return false;
        } else {
            boolean allDone = super.endElement(element);
            if (allDone && servletMappings!=null) {
                for (Iterator<String> keys = servletMappings.keySet().iterator(); keys.hasNext();) {
                    String servletName = keys.next();
                    Vector<String> mappings = servletMappings.get(servletName);
                    WebComponentDescriptor servlet= descriptor.getWebComponentByCanonicalName(servletName);
                    if (servlet!=null) {
                        for (Iterator<String> mapping = mappings.iterator();mapping.hasNext();) {
                            servlet.addUrlPattern(mapping.next());
                        }
                    } else {
                        throw new RuntimeException("There is no web component by the name of " + servletName + " here.");                    
                    } 
                }
            }
            return allDone;
        }
    }