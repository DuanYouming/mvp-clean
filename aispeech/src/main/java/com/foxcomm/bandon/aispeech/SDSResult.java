package com.foxcomm.bandon.aispeech;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SDSResult {
    private String version;
    private String applicationId;
    private String recordId;
    private String luabin;
    private ResultBean result;
    private int eof;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getLuabin() {
        return luabin;
    }

    public void setLuabin(String luabin) {
        this.luabin = luabin;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public int getEof() {
        return eof;
    }

    public void setEof(int eof) {
        this.eof = eof;
    }

    public static class ResultBean {


        private String input;
        private SemanticsBean semantics;
        private SdsBean sds;
        private String res;

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public SemanticsBean getSemantics() {
            return semantics;
        }

        public void setSemantics(SemanticsBean semantics) {
            this.semantics = semantics;
        }

        public SdsBean getSds() {
            return sds;
        }

        public void setSds(SdsBean sds) {
            this.sds = sds;
        }

        public String getRes() {
            return res;
        }

        public void setRes(String res) {
            this.res = res;
        }

        public static class SemanticsBean {

            private RequestBean request;

            public RequestBean getRequest() {
                return request;
            }

            public void setRequest(RequestBean request) {
                this.request = request;
            }

            public static class RequestBean {

                private int slotcount;
                private String domain;
                private String action;
                private ParamBean param;

                public int getSlotcount() {
                    return slotcount;
                }

                public void setSlotcount(int slotcount) {
                    this.slotcount = slotcount;
                }

                public String getDomain() {
                    return domain;
                }

                public void setDomain(String domain) {
                    this.domain = domain;
                }

                public String getAction() {
                    return action;
                }

                public void setAction(String action) {
                    this.action = action;
                }

                public ParamBean getParam() {
                    return param;
                }

                public void setParam(ParamBean param) {
                    this.param = param;
                }

                public static class ParamBean {

                    @SerializedName("操作")
                    private String operation;
                    @SerializedName("__act__")
                    private String action;
                    @SerializedName("对象")
                    private String object;

                    public String getOperation() {
                        return operation;
                    }

                    public void setOperation(String operation) {
                        this.operation = operation;
                    }

                    public String getAction() {
                        return action;
                    }

                    public void setAction(String action) {
                        this.action = action;
                    }

                    public String getObject() {
                        return object;
                    }

                    public void setObject(String object) {
                        this.object = object;
                    }
                }
            }
        }

        public static class SdsBean {
            /**
             * state : interact
             * log : {"input":{"uacts":[{"act_type":"restart","conf":1},{"slot_val":"收听","slot_name":"operation","act_type":"inform","conf":1}],"nlu":{"input":"我要听歌了","semantics":{"request":{"slotcount":3,"domain":"音乐","action":"音乐","param":{"操作":"收听","__act__":"inform","对象":"音乐"}}},"res":"aihome.0.17.11_0.14.13"}},"domain":"music","output":{"macts":[{"slot_val":"title","slot_name":"slot","act_type":"request","conf":1}],"nlg":"请问您要听什么歌？"}}
             * version : hnp-v1.2.4.2:[hnp:hnp-dev][res:20180828-11:59][src:20180404-14:17]
             * contextId : 0748a97359f318002a1cb76c
             * data : {}
             * domain : music
             * output : 请问您要听什么歌？
             */

            private String state;
            private LogBean log;
            private String version;
            private String contextId;
            private DataBean data;
            private String domain;
            private String output;

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public LogBean getLog() {
                return log;
            }

            public void setLog(LogBean log) {
                this.log = log;
            }

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }

            public String getContextId() {
                return contextId;
            }

            public void setContextId(String contextId) {
                this.contextId = contextId;
            }

            public DataBean getData() {
                return data;
            }

            public void setData(DataBean data) {
                this.data = data;
            }

            public String getDomain() {
                return domain;
            }

            public void setDomain(String domain) {
                this.domain = domain;
            }

            public String getOutput() {
                return output;
            }

            public void setOutput(String output) {
                this.output = output;
            }

            public static class LogBean {
                public static class InputBean {

                    private NluBean nlu;
                    private List<UactsBean> uacts;

                    public NluBean getNlu() {
                        return nlu;
                    }

                    public void setNlu(NluBean nlu) {
                        this.nlu = nlu;
                    }

                    public List<UactsBean> getUacts() {
                        return uacts;
                    }

                    public void setUacts(List<UactsBean> uacts) {
                        this.uacts = uacts;
                    }

                    public static class NluBean {

                        private String input;
                        private SemanticsBeanX semantics;
                        private String res;

                        public String getInput() {
                            return input;
                        }

                        public void setInput(String input) {
                            this.input = input;
                        }

                        public SemanticsBeanX getSemantics() {
                            return semantics;
                        }

                        public void setSemantics(SemanticsBeanX semantics) {
                            this.semantics = semantics;
                        }

                        public String getRes() {
                            return res;
                        }

                        public void setRes(String res) {
                            this.res = res;
                        }

                        public static class SemanticsBeanX {

                            private RequestBeanX request;

                            public RequestBeanX getRequest() {
                                return request;
                            }

                            public void setRequest(RequestBeanX request) {
                                this.request = request;
                            }

                            public static class RequestBeanX {

                                private int slotcount;
                                private String domain;
                                private String action;
                                private ParamBeanX param;

                                public int getSlotcount() {
                                    return slotcount;
                                }

                                public void setSlotcount(int slotcount) {
                                    this.slotcount = slotcount;
                                }

                                public String getDomain() {
                                    return domain;
                                }

                                public void setDomain(String domain) {
                                    this.domain = domain;
                                }

                                public String getAction() {
                                    return action;
                                }

                                public void setAction(String action) {
                                    this.action = action;
                                }

                                public ParamBeanX getParam() {
                                    return param;
                                }

                                public void setParam(ParamBeanX param) {
                                    this.param = param;
                                }

                                public static class ParamBeanX {
                                    @SerializedName("操作")
                                    private String operation;
                                    @SerializedName("__act__")
                                    private String action;
                                    @SerializedName("对象")
                                    private String object;

                                    public String getOperation() {
                                        return operation;
                                    }

                                    public void setOperation(String operation) {
                                        this.operation = operation;
                                    }

                                    public String getAction() {
                                        return action;
                                    }

                                    public void setAction(String action) {
                                        this.action = action;
                                    }

                                    public String getObject() {
                                        return object;
                                    }

                                    public void setObject(String object) {
                                        this.object = object;
                                    }
                                }
                            }
                        }
                    }

                    public static class UactsBean {

                        private String act_type;
                        private int conf;
                        private String slot_val;
                        private String slot_name;

                        public String getAct_type() {
                            return act_type;
                        }

                        public void setAct_type(String act_type) {
                            this.act_type = act_type;
                        }

                        public int getConf() {
                            return conf;
                        }

                        public void setConf(int conf) {
                            this.conf = conf;
                        }

                        public String getSlot_val() {
                            return slot_val;
                        }

                        public void setSlot_val(String slot_val) {
                            this.slot_val = slot_val;
                        }

                        public String getSlot_name() {
                            return slot_name;
                        }

                        public void setSlot_name(String slot_name) {
                            this.slot_name = slot_name;
                        }
                    }
                }

                public static class OutputBean {
                    public static class MactsBean {
                    }
                }
            }

            public static class DataBean {
            }
        }
    }
}
