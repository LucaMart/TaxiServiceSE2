    /**
     * write the descriptor class to a DOM tree and return it
     *
     * @param parent node for the DOM tree
     * @param webBundleDesc descriptor to write
     * @return the DOM tree top node
     */    
    public Node writeDescriptor(Node parent, 
        T webBundleDesc) {
        Node jarNode = super.writeDescriptor(parent, webBundleDesc);             
        if (webBundleDesc.isDistributable()) {
            appendChild(jarNode, WebTagNames.DISTRIBUTABLE);        
        }
        
        // context-param*
        addInitParam(jarNode, WebTagNames.CONTEXT_PARAM, webBundleDesc.getContextParametersSet());
        
        // filter*
        FilterNode filterNode = new FilterNode();
        for (Enumeration filters = webBundleDesc.getServletFilters().elements();filters.hasMoreElements();) {
            filterNode.writeDescriptor(jarNode, WebTagNames.FILTER, 
                                                     (ServletFilterDescriptor) filters.nextElement());
        }
        
        // filter-mapping*
        FilterMappingNode filterMappingNode = new FilterMappingNode();
        for (Enumeration mappings = webBundleDesc.getServletFilterMappings().elements();
              mappings.hasMoreElements();) {
            filterMappingNode.writeDescriptor(jarNode, WebTagNames.FILTER_MAPPING, 
                                                     (ServletFilterMappingDescriptor) mappings.nextElement());
        }        
        
        // listener*
        Vector appListeners = webBundleDesc.getAppListenerDescriptors();
        if (!appListeners.isEmpty()) {
            ListenerNode listenerNode = new ListenerNode();
            for (Enumeration e = appListeners.elements();e.hasMoreElements();) {
                listenerNode.writeDescriptor(jarNode, WebTagNames.LISTENER,
                        (AppListenerDescriptorImpl) e.nextElement());
            }
        }
        
        Set servlets = webBundleDesc.getWebComponentDescriptors();
        if (!servlets.isEmpty()) {
            // servlet*
            ServletNode servletNode = new ServletNode();
            for (Iterator  e= servlets.iterator();e.hasNext();) {
                WebComponentDescriptor aServlet = (WebComponentDescriptor) e.next();
                servletNode.writeDescriptor(jarNode, aServlet);
            }

            // servlet-mapping*        
            for (Iterator servletsIterator = servlets.iterator(); servletsIterator.hasNext();) {
                WebComponentDescriptor aServlet = (WebComponentDescriptor) servletsIterator.next();                
                for (Iterator patterns = aServlet.getUrlPatternsSet().iterator();patterns.hasNext();) {
                    String pattern = (String) patterns.next();
                    Node mappingNode= appendChild(jarNode, WebTagNames.SERVLET_MAPPING);
                    appendTextChild(mappingNode, WebTagNames.SERVLET_NAME, aServlet.getCanonicalName());
                    
                    // If URL Pattern does not start with "/" then
                    // prepend it (for 1.2 Web apps)                    
                    if (webBundleDesc.getSpecVersion().equals("2.2")) {
                        if (!pattern.startsWith("/") 
                            && !pattern.startsWith("*.")) {
                            pattern = "/" + pattern;
                        }                    
                    }
                    appendTextChild(mappingNode, WebTagNames.URL_PATTERN, pattern);
                }
            }
        }
        
        // mime-mapping*
        MimeMappingNode mimeNode = new MimeMappingNode();
        for (Enumeration e = webBundleDesc.getMimeMappings();e.hasMoreElements();) {
            MimeMappingDescriptor mimeMapping = (MimeMappingDescriptor) e.nextElement();
            mimeNode.writeDescriptor(jarNode, WebTagNames.MIME_MAPPING, mimeMapping);
        }
        
        // welcome-file-list?
        Enumeration welcomeFiles = webBundleDesc.getWelcomeFiles();
        if (welcomeFiles.hasMoreElements()) {
            Node welcomeList = appendChild(jarNode, WebTagNames.WELCOME_FILE_LIST);
            while (welcomeFiles.hasMoreElements()) {
                appendTextChild(welcomeList, WebTagNames.WELCOME_FILE,
                                (String) welcomeFiles.nextElement());
            }
        }
        
        // error-page*
        Enumeration errorPages = webBundleDesc.getErrorPageDescriptors();
        if (errorPages.hasMoreElements()) {
            ErrorPageNode errorPageNode = new ErrorPageNode();
            while (errorPages.hasMoreElements()) {
                errorPageNode.writeDescriptor(jarNode, WebTagNames.ERROR_PAGE, 
                                (ErrorPageDescriptor) errorPages.nextElement());
            }
        }
        
        // jsp-config *
	JspConfigDescriptorImpl jspConf = webBundleDesc.getJspConfigDescriptor();
	if(jspConf != null) {
	    JspConfigNode ln = new JspConfigNode();
	    ln.writeDescriptor(jarNode, 
				WebTagNames.JSPCONFIG,
				jspConf);
	}

        // security-constraint*
        Enumeration securityConstraints = webBundleDesc.getSecurityConstraints();
        if (securityConstraints.hasMoreElements()) {
            SecurityConstraintNode scNode = new SecurityConstraintNode();
            while (securityConstraints.hasMoreElements()) {
                SecurityConstraintImpl sc = (SecurityConstraintImpl) securityConstraints.nextElement();
                scNode.writeDescriptor(jarNode, WebTagNames.SECURITY_CONSTRAINT, sc);
            }
        }

        // login-config ?
        LoginConfigurationImpl lci = (LoginConfigurationImpl) webBundleDesc.getLoginConfiguration();
        if (lci!=null) {
            LoginConfigNode lcn = new LoginConfigNode();
            lcn.writeDescriptor(jarNode, WebTagNames.LOGIN_CONFIG, lci);
        }
        
        // security-role*
        Enumeration roles = webBundleDesc.getSecurityRoles();
        if (roles.hasMoreElements()) {
            SecurityRoleNode srNode = new SecurityRoleNode();
            while (roles.hasMoreElements()) {
                SecurityRoleDescriptor role = (SecurityRoleDescriptor) roles.nextElement();
                srNode.writeDescriptor(jarNode, WebTagNames.ROLE, role);            
            }
        }

        // env-entry*
        writeEnvEntryDescriptors(jarNode, webBundleDesc.getEnvironmentProperties().iterator());

        // ejb-ref * and ejb-local-ref*
        writeEjbReferenceDescriptors(jarNode, webBundleDesc.getEjbReferenceDescriptors().iterator());

        // service-ref*
        writeServiceReferenceDescriptors(jarNode, webBundleDesc.getServiceReferenceDescriptors().iterator());

        // resource-ref*
        writeResourceRefDescriptors(jarNode, webBundleDesc.getResourceReferenceDescriptors().iterator());
                
        // resource-env-ref*
        writeResourceEnvRefDescriptors(jarNode, webBundleDesc.getResourceEnvReferenceDescriptors().iterator());        

        // message-destination-ref*
        writeMessageDestinationRefDescriptors(jarNode, webBundleDesc.getMessageDestinationReferenceDescriptors().iterator());
        
        // persistence-context-ref*
        writeEntityManagerReferenceDescriptors(jarNode, webBundleDesc.getEntityManagerReferenceDescriptors().iterator());
        
        // persistence-unit-ref*
        writeEntityManagerFactoryReferenceDescriptors(jarNode, webBundleDesc.getEntityManagerFactoryReferenceDescriptors().iterator());
        
        // post-construct
        writeLifeCycleCallbackDescriptors(jarNode, TagNames.POST_CONSTRUCT, webBundleDesc.getPostConstructDescriptors());

        // pre-destroy
        writeLifeCycleCallbackDescriptors(jarNode, TagNames.PRE_DESTROY, webBundleDesc.getPreDestroyDescriptors());

        // all descriptors (includes DSD, MSD, JMSCFD, JMSDD,AOD, CFD)*
        writeResourceDescriptors(jarNode, webBundleDesc.getAllResourcesDescriptors().iterator());

        // message-destination*
        writeMessageDestinations
            (jarNode, webBundleDesc.getMessageDestinations().iterator());

        // locale-encoding-mapping-list
        LocaleEncodingMappingListDescriptor lemListDesc = webBundleDesc.getLocaleEncodingMappingListDescriptor();
        if (lemListDesc != null) {
            Node lemList = appendChild(jarNode, WebTagNames.LOCALE_ENCODING_MAPPING_LIST);
            LocaleEncodingMappingNode lemNode = new LocaleEncodingMappingNode();
            for (LocaleEncodingMappingDescriptor lemDesc : lemListDesc.getLocaleEncodingMappingSet()) {
                lemNode.writeDescriptor(lemList, WebTagNames.LOCALE_ENCODING_MAPPING, lemDesc);
            }
        }

        if (webBundleDesc.getSessionConfig() != null) {
            SessionConfigNode scNode = new SessionConfigNode();
            scNode.writeDescriptor(jarNode, WebTagNames.SESSION_CONFIG,
                    (SessionConfigDescriptor)webBundleDesc.getSessionConfig());            
        }

        return jarNode;
    }