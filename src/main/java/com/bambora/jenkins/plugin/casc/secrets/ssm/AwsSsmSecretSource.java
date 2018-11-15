package com.bambora.jenkins.plugin.casc.secrets.ssm;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.AWSSimpleSystemsManagementException;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.amazonaws.services.simplesystemsmanagement.model.ParameterNotFoundException;
import hudson.Extension;
import io.jenkins.plugins.casc.SecretSource;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension
public class AwsSsmSecretSource extends SecretSource {

    public static final String CASC_SSM_PREFIX = "CASC_SSM_PREFIX";

    private static final Logger LOG = Logger.getLogger(AwsSsmSecretSource.class.getName());

    @Override
    public Optional<String> reveal(String key) {
        String resolveKey = key;
        String prefix =  getSystemProperty();
        if (prefix != null) {
            resolveKey = prefix + key;
        }
        try {
            GetParameterRequest request = new GetParameterRequest();
            request.withName(resolveKey).withWithDecryption(true);
            GetParameterResult result = getClient().getParameter(request);
            return Optional.of(result.getParameter().getValue());
        } catch (ParameterNotFoundException e) {
            LOG.info("Could not find secret: " + resolveKey);
            return Optional.empty();
        } catch (AWSSimpleSystemsManagementException e) {
            LOG.log(Level.SEVERE, "Error getting ssm secret: " + resolveKey, e);
            return Optional.empty();
        }
    }

    private AWSSimpleSystemsManagement getClient() {
        AWSSimpleSystemsManagementClientBuilder builder = AWSSimpleSystemsManagementClientBuilder.standard();
        builder.setCredentials(new DefaultAWSCredentialsProviderChain());
        return builder.build();
    }

    private String getSystemProperty() {
        return System.getenv(CASC_SSM_PREFIX);
    }

}
