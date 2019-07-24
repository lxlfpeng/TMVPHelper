package pengge;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiJavaFile;

import java.io.IOException;

public class MVPHelperAction extends AnAction {
    private final int MODE_CONTRACT = 0;
    private final int MODE_PRESENTER = 1;
    private ClassModel _classModel;
    private String _content;
    private Editor _editor;
    private AnActionEvent _event;
    private String _path;
    private boolean canCreate;
    private int mode;

    public void actionPerformed(AnActionEvent e) {
        this._event = e;
        this.canCreate = true;
        init(e);
        getClassModel();
        createFiles();
        System.out.println("current package name is :" + ((PsiJavaFile) e.getData(CommonDataKeys.PSI_FILE)).getPackageName());
        try {
            if (this.canCreate) {
                createClassFiles();
                refreshProject(e);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void refreshProject(AnActionEvent e) {
        e.getProject().getBaseDir().refresh(false, true);
    }

    private void createClassFiles() throws IOException {
        if (this.mode == 0) {
            createFileWithContract();
        } else if (this.mode == 1) {
            createClassWithPresenter();
        }
    }

    private void createClassWithPresenter() throws IOException {
        String className = this._classModel.get_className();
        String classFullName = this._classModel.get_classFullName();
        System.out.println("_path presenter:" + this._path);
        ClassCreateHelper.createImplClass(this._path, className, classFullName, 0, 1);
        ClassCreateHelper.createImplClass(this._path, className, classFullName, 1, 1);

        ClassCreateHelper.createInterface(this._path, className, classFullName, 0);
        ClassCreateHelper.createInterface(this._path, className, classFullName, 2);
    }

    private void createFileWithContract() throws IOException {
        String className = this._classModel.get_className();
        String classFullName = this._classModel.get_classFullName();
        System.out.println("_path:" + this._path);
        //ClassCreateHelper.createImplClass(this._path, className, classFullName, 0, 0);
        ClassCreateHelper.createImplClass(this._path, className, classFullName, 1, 0);
    }

    private void createFiles() {
        if (this._classModel.get_className() != null) {
            this._path = ClassCreateHelper.getCurrentPath(this._event, this._classModel.get_classFullName());
            if (this._path.contains("contract")) {
                System.out.println("_path replace contract " + this._path);
                this._path = this._path.replace("contract/", "");
            } else if (this._path.contains("presenter")) {
                System.out.println("_path replace contract " + this._path);
                this._path = this._path.replace("presenter/", "");
            } else {
                if (this.mode == 0) {
                    MessagesCenter.showErrorMessage("Your Contract should in package 'contract'.", "error");
                } else if (this.mode == 1) {
                    MessagesCenter.showErrorMessage("Your Presenter should in package 'presenter'.", "error");
                }
                this.canCreate = false;
            }
            if (this.canCreate && this.mode == 0) {
                setFileDocument();
            }
        }
    }

    private void setFileDocument() {
        this._content = this._content.substring(0, this._content.lastIndexOf("}"));
        MessagesCenter.showDebugMessage(this._content, "debug");
        final String content = setContractContent();
        WriteCommandAction.runWriteCommandAction(this._editor.getProject(), new Runnable() {
            public void run() {
                MVPHelperAction.this._editor.getDocument().setText(content);
            }
        });
    }

    private String setContractContent() {
        String className = this._classModel.get_className();
        return this._content + "public interface View extends BaseView{\n\n}\n\nabstract class Presenter extends  BasePresenter<View>{\n\n}\n}";
    }

    private void getClassModel() {
        this._content = this._editor.getDocument().getText();
        for (String word : this._content.split(" ")) {
            String className;
            if (word.contains("Contract")) {
                className = word.substring(0, word.indexOf("Contract"));
                this._classModel.set_className(className);
                this._classModel.set_classFullName(word);
                MessagesCenter.showDebugMessage(className, "class name");
                this.mode = 0;
            } else if (word.contains("Presenter")) {
                className = word.substring(0, word.indexOf("Presenter"));
                this._classModel.set_className(className);
                this._classModel.set_classFullName(word);
                this.mode = 1;
                MessagesCenter.showDebugMessage(className, "class name");
            }
        }
        if (this._classModel.get_className() == null) {
            MessagesCenter.showErrorMessage("Create failed ,Can't found 'Contract' or 'Presenter' in your class name,your class name must contain 'Contract' or 'Presenter'", "error");
            this.canCreate = false;
        }
    }

    private void init(AnActionEvent e) {
        this._editor = (Editor) e.getData(PlatformDataKeys.EDITOR);
        this._classModel = new ClassModel();
    }
}