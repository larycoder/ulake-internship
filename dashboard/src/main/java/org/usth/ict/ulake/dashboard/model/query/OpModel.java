package org.usth.ict.ulake.dashboard.model.query;

public class OpModel {
    private String opName;

    public OpModel(String opName) {
        this.setOpName(opName);
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public Boolean verify(String data, String value) throws QueryException {
        if(data == null) return false;

        if (this.opName.equals("=")) {
            return data.equals(value);
        } else if (this.opName.equals(">")) {
            return data.compareTo(value) > 0;
        } else if (this.opName.equals("<")) {
            return data.compareTo(value) < 0;
        } else if (this.opName.equals("like")) {
            return data.matches(value);
        } else {
            throw new QueryException(
                "Operation " + opName + " is not supported with type String");
        }
    }

    public Boolean verify(Long data, String valueString) throws QueryException {
        if(data == null) return false;

        Long value = Long.parseLong(valueString);
        if (this.opName.equals("=")) {
            return data.equals(value);
        } else if (this.opName.equals(">")) {
            return data.compareTo(value) > 0;
        } else if (this.opName.equals("<")) {
            return data.compareTo(value) < 0;
        } else {
            throw new QueryException(
                "Operation " + opName + " is not supported with type Long");
        }
    }
}
