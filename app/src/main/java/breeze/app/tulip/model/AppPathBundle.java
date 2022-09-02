package breeze.app.tulip.model;

import java.util.List;

public class AppPathBundle {
    private String packageName;

    private List<MethodPath> methodPaths;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<MethodPath> getMethodPaths() {
        return methodPaths;
    }

    public void setMethodPaths(List<MethodPath> methodPaths) {
        this.methodPaths = methodPaths;
    }

    public static class MethodPath {
        private String methodName;
        private String value;
        private String parentClass;

        public String getParentClass() {
            return parentClass;
        }

        public void setParentClass(String parentClass) {
            this.parentClass = parentClass;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
