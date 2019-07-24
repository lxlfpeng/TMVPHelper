#Android studio的TMVp插件.
生成代码格式为:


	public interface TestContract {

    public interface View extends BaseView {

    }

    abstract class Presenter extends BasePresenter<View> {

    }
	}


presenter



	public class TestPresenter extends TestContract.Presenter {   
	}
