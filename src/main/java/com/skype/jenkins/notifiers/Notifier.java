package com.skype.jenkins.notifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.skype.jenkins.SkypeHelper;
import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.dto.NotifyTypeEnum;
import com.skype.jenkins.dto.ParametersDTO;
import com.skype.jenkins.logger.Logger;
import com.skype.jenkins.rest.JenkinsRestHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public abstract class Notifier {

    protected final String jenkinsUrl;
    protected final ConfigJobDTO jobConfig;

    public Notifier(final String jenkinsUrl, final ConfigJobDTO jobConfig) {
        this.jenkinsUrl = jenkinsUrl;
        this.jobConfig = jobConfig;
    }

    public abstract void composeSendNotifications();

    protected void sendNotifications(final List<String> messages) {
        messages.forEach(notif -> SkypeHelper.sendSkype(notif, jobConfig.getInfo().getChatId()));
    }

    protected void addJenkinsResponseToSkypeBotMessages(final BuildWithDetails jenkinsJobDTO, final List<String> messages) {
        messages.add(composeMessageFromJenkinsResponse(jenkinsJobDTO));
    }

    protected String composeMessageFromJenkinsResponse(final BuildWithDetails jobResult) {
        StringBuilder textOutput = new StringBuilder();
        textOutput.append(Optional.ofNullable(
                jobConfig.getNotifierByType(NotifyTypeEnum.getNotifierByClass(this.getClass()))
                        .getMessage()).map(message -> publishBuildMessage(message)).orElse(""));
        textOutput.append(publishParameters(prepareAllParameters(jobResult)));
        textOutput.append(Optional.ofNullable(
                jobConfig.getNotifierByType(NotifyTypeEnum.getNotifierByClass(this.getClass()))
                        .getLineFromLog()).map(text -> publishConsole(text)).orElse(""));
        textOutput.append(jobResult.getUrl() + "\n");
        textOutput.append(getThucydidesReport(jobResult));
        return textOutput.toString();
    }

    private String publishBuildMessage(final String message) {
        StringBuilder textOutput = new StringBuilder();
        textOutput.append(message);
        Logger.out.debug(message);
        return textOutput.append("\n").toString();
    }

    private List<ParametersDTO> prepareAllParameters(final BuildWithDetails jobResult) {
        List<ParametersDTO> parameters = new ArrayList<>();
        parameters.addAll(jobConfig.getDefaultParameters());
        parameters.addAll(jobConfig.getNotifierByType(NotifyTypeEnum.getNotifierByClass(this.getClass())).getParameters());
        return parameters.stream().peek(par -> par.setValue(jobResult.getParameters().get(par.getName())))
                .filter(par -> null != par.getValue()).collect(Collectors.toList());
    }

    private String publishParameters(final List<ParametersDTO> parameters) {
        StringBuilder textOutput = new StringBuilder();
        for (ParametersDTO param : parameters) {
            String paramMessage = (null == param.getMessage()) ? param.getName() + " : " + param.getValue()
                    : String.format(param.getMessage(), param.getValue());
            textOutput.append(paramMessage).append("\n");
            Logger.out.debug(paramMessage);
        }
        return textOutput.toString();
    }

    private String publishConsole(final String text) {
        String textOutput = JenkinsRestHelper.getInstance(jenkinsUrl).getJobConsole(jobConfig.getInfo().getJobName())
                .stream().filter(line -> line.contains(text)).collect(Collectors.joining("\n"));
        Logger.out.debug(textOutput);
        return textOutput;
    }

    private String getThucydidesReport(final BuildWithDetails result) {
        StringBuilder thucydidesResult = new StringBuilder("");
        if (!result.isBuilding() && (BuildResult.SUCCESS.equals(result.getResult()) || BuildResult.UNSTABLE.equals(result.getResult()))) {
            String report = JenkinsRestHelper.getInstance(jenkinsUrl).getJenkinsJobThucydides(jobConfig.getInfo().getJobName(), result.getNumber());
            if (report.isEmpty()) {
                return "";
            } else {
                thucydidesResult.append("Serenity Result\n");
            }
            Document doc = Jsoup.parse(report);
            Elements summary = doc.select(".summary-leading-column").get(0).parents();
            thucydidesResult.append("test passed: ").append(summary.select("td").get(2).text()).append("\n");
            thucydidesResult.append("test failed: ").append(summary.select("td").get(3).text()).append("\n");
            thucydidesResult.append("report Url: ").append(JenkinsRestHelper.getInstance(jenkinsUrl).getThucydidesUrl(jobConfig.getInfo().getJobName(), result.getNumber()))
                    .append("\n");
        }
        Logger.out.debug(thucydidesResult);
        return thucydidesResult.toString();
    }
}
