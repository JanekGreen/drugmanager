package pwojcik.pl.archcomponentstestproject.Utils;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by pawel on 05.02.18.
 */

public class DisposableManager {
    private static DisposableManager disposableManager;
   private CompositeDisposable compositeDisposable;

    private DisposableManager(){
       compositeDisposable = new CompositeDisposable();
    }

    public static DisposableManager getInstance(){
        if(disposableManager == null){
            disposableManager = new DisposableManager();
        }
        return disposableManager;
    }

    public void add(Disposable disposable){
        compositeDisposable.add(disposable);
    }

    public void dispose(){
        compositeDisposable.dispose();
    }
}
