package com.skype.jenkins.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.skype.jenkins.dto.JenkinsJobDTO.ActionsDto.CausesDto;

public class JenkinsJobDTO {
    
    @SerializedName("actions")
    private List<ActionsDto> actions;
    
    @SerializedName("number")
    private int number;
    
    @SerializedName("building")
    private boolean building;
    
    //if run - get previous status. look at building value
    @SerializedName("result")
    private JobResultEnum result;
    
    @SerializedName("url")
    private String url;
    
    @SerializedName("displayName")
    private String displayName;
    
    @SerializedName("fullDisplayName")
    private String fullDisplayName;
    
    
    
    private List<ActionsDto> getActions() {
        return actions;
    }



    public void setActions(List<ActionsDto> actions) {
        this.actions = actions;
    }



    public int getNumber() {
        return number;
    }



    public void setNumber(int number) {
        this.number = number;
    }



    public boolean isBuilding() {
        return building;
    }



    public void setBuilding(boolean building) {
        this.building = building;
    }



    public JobResultEnum getResult() {
        return result;
    }



    public void setResult(JobResultEnum result) {
        this.result = result;
    }



    public String getUrl() {
        return url;
    }



    public void setUrl(String url) {
        this.url = url;
    }



    public String getDisplayName() {
        return displayName;
    }



    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }



    public String getFullDisplayName() {
        return fullDisplayName;
    }



    public void setFullDisplayName(String fullDisplayName) {
        this.fullDisplayName = fullDisplayName;
    }
    
    public List<ParametersDTO> getParameters() {
        return this.getActions().stream().filter(act -> null != act.getParameters()).findAny().map(act -> act.getParameters()).orElse(new ArrayList<>());
    }
    
    public ParametersDTO getParameterByName(String name) {
        return getParameters().stream().filter(param -> name.equals(param.getName())).findFirst().orElseGet(ParametersDTO::new);
    }

    public List<CausesDto> getCauses() {
        return this.getActions().stream().filter(act -> null != act.getCauses()).findAny().map(act -> act.getCauses()).orElse(new ArrayList<>());
    }


    protected class ActionsDto {
        @SerializedName("parameters")
        private List<ParametersDTO> parameters;
        
        @SerializedName("causes")
        private List<CausesDto> causes;
        
        
        
        private List<ParametersDTO> getParameters() {
            return parameters;
        }



        public void setParameters(List<ParametersDTO> parameters) {
            this.parameters = parameters;
        }



        private List<CausesDto> getCauses() {
            return causes;
        }



        public void setCauses(List<CausesDto> causes) {
            this.causes = causes;
        }



        protected class CausesDto {
            @SerializedName("shortDescription")
            private String shortDescription;
            @SerializedName("userId")
            private String userId;
            @SerializedName("userName")
            private String userName;
            
            @SerializedName("upstreamBuild")
            private int upstreamBuild;
            @SerializedName("upstreamProject")
            private String upstreamProject;
            public String getShortDescription() {
                return shortDescription;
            }
            public void setShortDescription(String shortDescription) {
                this.shortDescription = shortDescription;
            }
            public String getUserId() {
                return userId;
            }
            public void setUserId(String userId) {
                this.userId = userId;
            }
            public String getUserName() {
                return userName;
            }
            public void setUserName(String userName) {
                this.userName = userName;
            }
            public int getUpstreamBuild() {
                return upstreamBuild;
            }
            public void setUpstreamBuild(int upstreamBuild) {
                this.upstreamBuild = upstreamBuild;
            }
            public String getUpstreamProject() {
                return upstreamProject;
            }
            public void setUpstreamProject(String upstreamProject) {
                this.upstreamProject = upstreamProject;
            }
            
            
        }
        
    }
    
}
