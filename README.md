# utils
android utils

Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  	}
Step 2. Add the dependency

	dependencies {
    /**
     * SNAPSHOT版本
     * 分支名称-SNAPSHOT
     * 适用频繁改源码的开发阶段.
     * 好处:修改源码后,只需要把对应的分支push到github即可. 使用者重新拉去最新的依赖.
     */
    implementation 'com.github.lex-android:utils:master-SNAPSHOT'

    /**
     * Release发布版本
     * 代码push完成后,要到github创建一个 release 版本. Draft a new release
     * 使用者需要把依赖后的版本号改成github上对应的最新版本号.
     * implementation 'com.github.lex-android:utils:Tag'
     */
    implementation 'com.github.lex-android:utils:0.0.1'
	}



使用者如果拉取代码失败,还需要app的build.gradle中的 dependencies的同级别中添加一下代码. 强制拉去最新

configurations.all {
    resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
}
