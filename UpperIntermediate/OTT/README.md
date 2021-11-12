## OTT 앱 인트로
- 모션 레이아웃을 활용하여 OTT 앱과 같이 애니메이션 처리를 함

- 헤더 영역으로 AppBar를 직접 만들고 CollapsingToolbar와 Inset을 직접 설정하여 OTT 앱처럼 스크롤 시 애니메이션 처리를 함

- 직접 Toolbar 영역을 그리고 스크롤을 하면서 자연스럽게 애니메이션 전환들이 이루어짐, 그러기 위해서 직접 코드로 그 기준을 처리함

### 메인화면
![one](/UpperIntermediate/OTT/img/one.png)
![one](/UpperIntermediate/OTT/img/two.png)
![one](/UpperIntermediate/OTT/img/three.png)

- 처음에는 Toolbar를 직접 커스텀한 부분부터 시작해서 CollapsingToolbarLayout으로 설정하였으므로 스크롤을 내리면 Toolbar 부분이 자연스럽게 사라짐

- 그러면서 자연스럽게 그 밑의 text가 올라오고 그 다음 2번째 사진과 같이 배경인 Image와 각 기기의 이미지가 모이듯이 나타남, 애니메이션 처리를 함

- 그리고 자연스럽게 text가 나오고 마지막에 도형들의 애니메이션 처리가 처음에는 4개의 도형 마지막에는 퍼지면서 큰 도형이 하나 나타나는 형태로 처리함

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/coordinator"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="false"
    android:background="@color/black"
    tools:context=".MainActivity">

    <androidx.constraintlayout.motion.widget.MotionLayout
        android:id="@+id/buttonShown_MotionLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layoutDescription="@xml/button_shown_scene"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <androidx.core.widget.NestedScrollView
            android:id="@+id/scrollView"
            android:layout_width="0dp"
            android:layout_height="0dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical">

                <androidx.constraintlayout.widget.ConstraintLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:background="@color/black"
                    android:paddingTop="16dp">

                    <TextView
                        android:id="@+id/introduceContentsTitleTextView"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="영화, 드라마, 예능 등 8만 편의 작품"
                        android:textColor="@color/white"
                        android:textSize="16sp"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toTopOf="parent" />

                    <Button
                        android:id="@+id/useTwoWeeksButton"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="32dp"
                        android:layout_marginBottom="200dp"
                        android:paddingHorizontal="24dp"
                        android:paddingVertical="12dp"
                        android:text="2주 무료 이용"
                        android:textSize="16sp"
                        android:textStyle="bold"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintTop_toBottomOf="@id/introduceContentsTitleTextView" />

                </androidx.constraintlayout.widget.ConstraintLayout>

                <androidx.constraintlayout.motion.widget.MotionLayout
                    android:id="@+id/gatheringDigitalThingsBackgroundMotionLayout"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    app:layoutDescription="@xml/gathering_digital_things_background_scene">

                    <ImageView
                        android:id="@+id/gatheringDigitalThingsBackgroundImageView"
                        android:layout_width="0dp"
                        android:layout_height="400dp"
                        android:scaleType="centerCrop"
                        android:src="@drawable/img_killingeve"
                        app:layout_constraintTop_toTopOf="parent"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"/>

                    <View
                        android:id="@+id/backgroundDimView"
                        android:layout_width="0dp"
                        android:layout_height="0dp"
                        android:background="@color/black"
                        app:layout_constraintTop_toTopOf="@id/gatheringDigitalThingsBackgroundImageView"
                        app:layout_constraintStart_toStartOf="@id/gatheringDigitalThingsBackgroundImageView"
                        app:layout_constraintEnd_toEndOf="@id/gatheringDigitalThingsBackgroundImageView"
                        app:layout_constraintBottom_toBottomOf="@id/gatheringDigitalThingsBackgroundImageView"/>

                    <androidx.constraintlayout.motion.widget.MotionLayout
                        android:id="@+id/gatheringDigitalThingsLayout"
                        android:layout_width="480dp"
                        android:layout_height="300dp"
                        android:layout_marginTop="120dp"
                        android:layout_gravity="center_horizontal"
                        app:layout_constraintTop_toTopOf="@id/gatheringDigitalThingsBackgroundImageView"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layoutDescription="@xml/gathering_digital_things_scene">

                        <ImageView
                            android:id="@+id/tvImageView"
                            android:layout_width="400dp"
                            android:layout_height="250dp"
                            android:scaleType="centerCrop"
                            android:src="@drawable/ic_tv"
                            app:layout_constraintBottom_toBottomOf="parent"
                            app:layout_constraintEnd_toEndOf="parent"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintTop_toTopOf="parent" />

                        <ImageView
                            android:id="@+id/tabletImageView"
                            android:layout_width="200dp"
                            android:layout_height="100dp"
                            android:scaleType="centerCrop"
                            android:src="@drawable/ic_tablet"
                            app:layout_constraintBottom_toBottomOf="parent"
                            app:layout_constraintEnd_toEndOf="parent"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintTop_toTopOf="parent" />

                        <ImageView
                            android:id="@+id/laptopImageView"
                            android:layout_width="200dp"
                            android:layout_height="150dp"
                            android:scaleType="centerCrop"
                            android:src="@drawable/ic_laptop"
                            app:layout_constraintBottom_toBottomOf="parent"
                            app:layout_constraintEnd_toEndOf="parent"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintTop_toTopOf="parent" />

                        <ImageView
                            android:id="@+id/phoneImageView"
                            android:layout_width="100dp"
                            android:layout_height="130dp"
                            android:scaleType="centerCrop"
                            android:src="@drawable/ic_phone"
                            app:layout_constraintBottom_toBottomOf="parent"
                            app:layout_constraintEnd_toEndOf="parent"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintTop_toTopOf="parent" />


                    </androidx.constraintlayout.motion.widget.MotionLayout>

                    <TextView
                        android:id="@+id/gatheringDigitalThingsTitleTextView"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="32dp"
                        android:gravity="center_horizontal"
                        android:text="다양한 디바이스에서\n자유롭게 감상"
                        android:textColor="@color/white"
                        android:textSize="24sp"
                        android:textStyle="bold"
                        app:layout_constraintTop_toTopOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"/>

                    <TextView
                        android:id="@+id/gatheringDigitalTHingsContentTextView"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="8dp"
                        android:gravity="center_horizontal"
                        android:text="PC, 태블릿, 펀, 크롬캐스트, TV\n어디서나 최고의 화질로"
                        android:textColor="@color/white_60"
                        android:textSize="16sp"
                        app:layout_constraintTop_toBottomOf="@id/gatheringDigitalThingsTitleTextView"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"/>

                </androidx.constraintlayout.motion.widget.MotionLayout>

                <androidx.constraintlayout.widget.ConstraintLayout
                    android:layout_width="match_parent"
                    android:layout_height="600dp"
                    android:layout_marginBottom="80dp">

                    <TextView
                        android:id="@+id/curationTitleTextView"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:gravity="center_horizontal"
                        android:text="5억개 평가 데이터 기반\n정확한 추천"
                        android:textColor="@color/white"
                        android:textSize="24sp"
                        android:textStyle="bold"
                        app:layout_constraintTop_toTopOf="parent"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"/>

                    <TextView
                        android:id="@+id/curationContentTextView"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="8dp"
                        android:gravity="center_horizontal"
                        android:text="취향에 안 맞는 작품에\n두 시간을 낭비할 순 없으니까"
                        android:textColor="@color/white_60"
                        android:textSize="16sp"
                        android:textStyle="bold"
                        app:layout_constraintTop_toBottomOf="@id/curationTitleTextView"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"/>

                    <androidx.constraintlayout.motion.widget.MotionLayout
                        android:id="@+id/curationAnimationMotionLayout"
                        android:layout_width="480dp"
                        android:layout_height="480dp"
                        app:layout_constraintStart_toStartOf="parent"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintBottom_toBottomOf="parent"
                        app:layoutDescription="@xml/curation_animation_scene">

                        <View
                            android:id="@+id/redView"
                            android:layout_width="120dp"
                            android:layout_height="120dp"
                            app:layout_constraintTop_toTopOf="parent"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintEnd_toEndOf="parent"
                            app:layout_constraintBottom_toBottomOf="parent"
                            android:background="@drawable/shape_circle"
                            android:backgroundTint="@color/red"/>

                        <View
                            android:id="@+id/yellowView"
                            android:layout_width="120dp"
                            android:layout_height="120dp"
                            app:layout_constraintTop_toTopOf="parent"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintEnd_toEndOf="parent"
                            app:layout_constraintBottom_toBottomOf="parent"
                            android:background="@drawable/shape_circle"
                            android:backgroundTint="@color/yellow"/>

                        <View
                            android:id="@+id/greenView"
                            android:layout_width="120dp"
                            android:layout_height="120dp"
                            app:layout_constraintTop_toTopOf="parent"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintEnd_toEndOf="parent"
                            app:layout_constraintBottom_toBottomOf="parent"
                            android:background="@drawable/shape_circle"
                            android:backgroundTint="@color/green"/>

                        <View
                            android:id="@+id/blueView"
                            android:layout_width="120dp"
                            android:layout_height="120dp"
                            app:layout_constraintTop_toTopOf="parent"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintEnd_toEndOf="parent"
                            app:layout_constraintBottom_toBottomOf="parent"
                            android:background="@drawable/shape_circle"
                            android:backgroundTint="@color/blue"/>

                        <View
                            android:id="@+id/centerView"
                            android:layout_width="180dp"
                            android:layout_height="180dp"
                            android:alpha="0"
                            android:background="@drawable/shape_circle"
                            android:backgroundTint="@color/white"
                            app:layout_constraintTop_toTopOf="parent"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintEnd_toEndOf="parent"
                            app:layout_constraintBottom_toBottomOf="parent"/>


                    </androidx.constraintlayout.motion.widget.MotionLayout>


                </androidx.constraintlayout.widget.ConstraintLayout>

            </LinearLayout>
        </androidx.core.widget.NestedScrollView>

        <Button
            android:id="@+id/button"
            android:layout_width="0dp"
            android:layout_height="64dp"
            android:layout_marginStart="24dp"
            android:layout_marginEnd="24dp"
            android:text="2주 무료 이용"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

    </androidx.constraintlayout.motion.widget.MotionLayout>

    <com.google.android.material.appbar.AppBarLayout
        android:id="@+id/appBar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@android:color/transparent"
        android:fitsSystemWindows="false"
        android:theme="@style/AppTheme.AppBarOverlay">

        <com.google.android.material.appbar.CollapsingToolbarLayout
            android:id="@+id/collapsingToolbar"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@android:color/transparent"
            android:fitsSystemWindows="false"
            app:contentScrim="@android:color/transparent"
            app:expandedTitleGravity="top"
            app:layout_scrollFlags="scroll|exitUntilCollapsed"
            app:statusBarScrim="@android:color/transparent">

            <androidx.constraintlayout.widget.ConstraintLayout
                android:id="@+id/collapsingToolbarContainer"
                android:layout_width="match_parent"
                android:layout_height="420dp"
                android:fitsSystemWindows="false"
                app:layout_collapseMode="parallax">

                <ImageView
                    android:id="@+id/introImageView"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:fitsSystemWindows="false"
                    android:scaleType="centerCrop"
                    android:src="@drawable/img_intro" />

            </androidx.constraintlayout.widget.ConstraintLayout>

            <include
                android:id="@+id/introTitleLayout"
                layout="@layout/layout_intro_title"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_gravity="bottom"
                app:layout_scrollFlags="scroll" />

            <androidx.appcompat.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@android:color/transparent"
                android:elevation="0dp"
                android:fitsSystemWindows="false"
                app:layout_collapseMode="pin"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintTop_toTopOf="parent">

                <FrameLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:fitsSystemWindows="true">

                    <View
                        android:id="@+id/toolbarBackgroundView"
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:background="@color/black"
                        android:fitsSystemWindows="false" />

                    <androidx.constraintlayout.widget.ConstraintLayout
                        android:id="@+id/toolbarContainer"
                        android:layout_width="match_parent"
                        android:layout_height="?attr/actionBarSize"
                        android:fitsSystemWindows="true">

                        <TextView
                            android:id="@+id/logoImageView"
                            android:layout_width="120dp"
                            android:layout_height="wrap_content"
                            android:gravity="center"
                            android:text="Watching"
                            android:textColor="@color/white"
                            android:textSize="24sp"
                            android:textStyle="bold"
                            app:layout_constraintBottom_toBottomOf="parent"
                            app:layout_constraintEnd_toEndOf="parent"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintTop_toTopOf="parent" />

                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginEnd="16dp"
                            android:text="로그인"
                            android:textColor="@color/white"
                            app:layout_constraintBottom_toBottomOf="parent"
                            app:layout_constraintEnd_toEndOf="parent"
                            app:layout_constraintTop_toTopOf="parent" />

                    </androidx.constraintlayout.widget.ConstraintLayout>

                </FrameLayout>

            </androidx.appcompat.widget.Toolbar>

        </com.google.android.material.appbar.CollapsingToolbarLayout>

    </com.google.android.material.appbar.AppBarLayout>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```
- 레이아웃 설계가 다소 복잡하긴하나 이 부분을 각각 MotionLayout으로 적절히 처리하였음

### button_shown_scene.xml
- 초반 첫 화면에 나오는 버튼과 동일하고 이 버튼은 그대로 처음에 두고 그 다음 button이라는 아이디를 가진 컴포넌트를 스크롤하면 나타나고 처리하기 위해서 애니메이션 처리를 함
```xml
<?xml version="1.0" encoding="utf-8"?>
<MotionScene xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:motion="http://schemas.android.com/apk/res-auto">

    <Transition
        app:constraintSetEnd="@id/end"
        app:constraintSetStart="@+id/start"
        app:duration="500">

        <KeyFrameSet>

            <KeyAttribute
                motion:framePosition="0"
                motion:motionTarget="@id/button"
                motion:transitionEasing="decelerate"
                android:alpha="0"/>

            <KeyAttribute
                motion:framePosition="100"
                motion:motionTarget="@id/button"
                motion:transitionEasing="decelerate"
                android:alpha="1"/>
        </KeyFrameSet>
    </Transition>

    <ConstraintSet android:id="@+id/start">
        <Constraint android:id="@+id/button"
            android:layout_width="0dp"
            android:layout_height="64dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="1.4"
            android:layout_marginStart="24dp"
            android:layout_marginEnd="24dp"/>
    </ConstraintSet>

    <ConstraintSet android:id="@+id/end">
        <Constraint android:id="@+id/button"
            android:layout_width="0dp"
            android:layout_height="64dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.97"
            android:layout_marginStart="24dp"
            android:layout_marginEnd="24dp"/>
    </ConstraintSet>
</MotionScene>

```

### gathering_digital_things_background.xml
- 첫 화면 이후 나타나는 화면에 대해서 스크롤 시 나올 수 있게 애니메이션 처리한 부분, 그리고 관련 Text에 대해서 전자기기들이 나올때 자연스럽게 나오게끔 애니메이션 처리를 함
```xml
<?xml version="1.0" encoding="utf-8"?>
<MotionScene xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <Transition
        app:constraintSetEnd="@id/end"
        app:constraintSetStart="@+id/start"
        app:duration="500">

    </Transition>

    <ConstraintSet android:id="@+id/start">

        <Constraint
            android:id="@+id/gatheringDigitalThingsBackgroundImageView"
            app:layout_constraintEnd_toEndOf="parent"
            android:layout_width="0dp"
            android:layout_height="400dp"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintStart_toStartOf="parent" />

        <Constraint
            android:id="@+id/backgroundDimView"
            android:layout_width="0dp"
            android:layout_height="0dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            android:alpha="0"/>

        <Constraint
            android:id="@+id/gatheringDigitalThingsTitleTextView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            android:layout_marginTop="32dp"
            android:gravity="center_horizontal"
            android:alpha="0"/>

        <Constraint
            android:id="@+id/gatheringDigitalTHingsContentTextView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toBottomOf="@id/gatheringDigitalThingsTitleTextView"
            android:layout_marginTop="8dp"
            android:gravity="center_horizontal"
            android:alpha="0"/>

    </ConstraintSet>

    <ConstraintSet android:id="@+id/end">

        <Constraint
            android:id="@+id/gatheringDigitalThingsBackgroundImageView"
            app:layout_constraintEnd_toEndOf="parent"
            android:layout_width="0dp"
            android:layout_height="400dp"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintStart_toStartOf="parent" />

        <Constraint
            android:id="@+id/backgroundDimView"
            android:layout_width="0dp"
            android:layout_height="0dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            android:alpha="0.7"/>

        <Constraint
            android:id="@+id/gatheringDigitalThingsTitleTextView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            android:layout_marginTop="32dp"
            android:gravity="center_horizontal"
            android:alpha="1"/>

        <Constraint
            android:id="@+id/gatheringDigitalTHingsContentTextView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toBottomOf="@id/gatheringDigitalThingsTitleTextView"
            android:layout_marginTop="8dp"
            android:gravity="center_horizontal"
            android:alpha="1"/>

    </ConstraintSet>


</MotionScene>

```

### gathering_digital_things_scene.xml
- 두번째 화면 이미지가 나타나고 전자기기들이 하나로 모이는 애니메이션을 나타내기 위한 처리, text의 경우 background에서 함께 처리함
```xml
<?xml version="1.0" encoding="utf-8"?>
<MotionScene xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:motion="http://schemas.android.com/apk/res-auto">

    <Transition
        app:constraintSetEnd="@id/end"
        app:constraintSetStart="@+id/start"
        app:duration="500">

        <KeyFrameSet>
            <KeyAttribute
                android:alpha="0"
                android:scaleX="0.8"
                android:scaleY="0.8"
                motion:framePosition="0"
                motion:motionTarget="@id/tabletImageView"
                motion:transitionEasing="decelerate" />

            <KeyAttribute
                android:alpha="1"
                android:scaleX="1"
                android:scaleY="1"
                motion:framePosition="100"
                motion:motionTarget="@id/tabletImageView"
                motion:transitionEasing="decelerate" />

        </KeyFrameSet>

        <KeyFrameSet>
            <KeyAttribute
                android:alpha="0"
                android:scaleX="0.9"
                android:scaleY="0.9"
                motion:framePosition="0"
                motion:motionTarget="@id/tvImageView"
                motion:transitionEasing="decelerate" />

            <KeyAttribute
                android:alpha="1"
                android:scaleX="1"
                android:scaleY="1"
                motion:framePosition="100"
                motion:motionTarget="@id/tvImageView"
                motion:transitionEasing="decelerate" />

        </KeyFrameSet>

        <KeyFrameSet>
            <KeyAttribute
                android:alpha="0"
                android:scaleX="0.8"
                android:scaleY="0.8"
                motion:framePosition="0"
                motion:motionTarget="@id/laptopImageView"
                motion:transitionEasing="decelerate" />

            <KeyAttribute
                android:alpha="1"
                android:scaleX="1"
                android:scaleY="1"
                motion:framePosition="100"
                motion:motionTarget="@id/laptopImageView"
                motion:transitionEasing="decelerate" />

        </KeyFrameSet>

        <KeyFrameSet>
            <KeyAttribute
                android:alpha="0"
                android:scaleX="0.8"
                android:scaleY="0.8"
                motion:framePosition="0"
                motion:motionTarget="@id/phoneImageView"
                motion:transitionEasing="decelerate" />

            <KeyAttribute
                android:alpha="1"
                android:scaleX="1"
                android:scaleY="1"
                motion:framePosition="100"
                motion:motionTarget="@id/phoneImageView"
                motion:transitionEasing="decelerate" />

        </KeyFrameSet>
    </Transition>

    <ConstraintSet android:id="@+id/start">

        <Constraint
            android:id="@+id/tabletImageView"
            android:layout_width="200dp"
            android:layout_height="100dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintHorizontal_bias="0"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintVertical_bias="0.8" />

        <Constraint
            android:id="@+id/tvImageView"
            android:layout_width="400dp"
            android:layout_height="250dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintVertical_bias="0.9" />

        <Constraint
            android:id="@+id/laptopImageView"
            android:layout_width="250dp"
            android:layout_height="150dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintHorizontal_bias="1"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintVertical_bias="0.8" />

        <Constraint
            android:id="@+id/phoneImageView"
            android:layout_width="100dp"
            android:layout_height="130dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintHorizontal_bias="1"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintVertical_bias="0.8" />

    </ConstraintSet>

    <ConstraintSet android:id="@+id/end">

        <Constraint
            android:id="@+id/tabletImageView"
            android:layout_width="200dp"
            android:layout_height="100dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintHorizontal_bias="0.1"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintVertical_bias="0.75" />

        <Constraint
            android:id="@+id/tvImageView"
            android:layout_width="400dp"
            android:layout_height="250dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <Constraint
            android:id="@+id/laptopImageView"
            android:layout_width="250dp"
            android:layout_height="150dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintHorizontal_bias="0.8"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintVertical_bias="0.75" />

        <Constraint
            android:id="@+id/phoneImageView"
            android:layout_width="100dp"
            android:layout_height="130dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintHorizontal_bias="0.9"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintVertical_bias="0.7" />

    </ConstraintSet>


</MotionScene>

```

### curation_animation_scene.xml
- 마지막에 도형이 나오는 애니메이션을 처리함, 여기서 맨 먼저 4개의 도형이 나오고 그 다음 가운데의 큰 원이 나타나기 때문에 각각 애니메이션 처리를 별도로 구분함
```xml
<?xml version="1.0" encoding="utf-8"?>
<MotionScene xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:motion="http://schemas.android.com/apk/res-auto">

    <Transition
        app:constraintSetStart="@+id/curation_animation_start1"
        app:constraintSetEnd="@id/curation_animation_end1"
        app:duration="500"
        app:motionInterpolator="easeIn"/>

    <Transition
        app:constraintSetStart="@+id/curation_animation_start2"
        app:constraintSetEnd="@id/curation_animation_end2"
        app:duration="500"
        app:motionInterpolator="easeIn"/>

    <ConstraintSet android:id="@+id/curation_animation_start1">
        <Constraint android:id="@+id/redView"
            android:layout_width="120dp"
            android:layout_height="120dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.3"
            app:layout_constraintHorizontal_bias="0.3"
            android:scaleX="0.5"
            android:scaleY="0.5"
            android:alpha="0"/>
        <Constraint android:id="@+id/yellowView"
            android:layout_width="120dp"
            android:layout_height="120dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.3"
            app:layout_constraintHorizontal_bias="0.7"
            android:scaleX="0.5"
            android:scaleY="0.5"
            android:alpha="0"/>
        <Constraint android:id="@+id/greenView"
            android:layout_width="120dp"
            android:layout_height="120dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.7"
            app:layout_constraintHorizontal_bias="0.3"
            android:scaleX="0.5"
            android:scaleY="0.5"
            android:alpha="0"/>
        <Constraint android:id="@+id/blueView"
            android:layout_width="120dp"
            android:layout_height="120dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.7"
            app:layout_constraintHorizontal_bias="0.7"
            android:scaleX="0.5"
            android:scaleY="0.5"
            android:alpha="0"/>
    </ConstraintSet>

    <ConstraintSet android:id="@+id/curation_animation_end1">
        <Constraint android:id="@+id/redView"
            android:layout_width="120dp"
            android:layout_height="120dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.3"
            app:layout_constraintHorizontal_bias="0.3"
            android:scaleX="1"
            android:scaleY="1"
            android:alpha="1"/>
        <Constraint android:id="@+id/yellowView"
            android:layout_width="120dp"
            android:layout_height="120dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.3"
            app:layout_constraintHorizontal_bias="0.7"
            android:scaleX="1"
            android:scaleY="1"
            android:alpha="1"/>
        <Constraint android:id="@+id/greenView"
            android:layout_width="120dp"
            android:layout_height="120dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.7"
            app:layout_constraintHorizontal_bias="0.3"
            android:scaleX="1"
            android:scaleY="1"
            android:alpha="1"/>
        <Constraint android:id="@+id/blueView"
            android:layout_width="120dp"
            android:layout_height="120dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.7"
            app:layout_constraintHorizontal_bias="0.7"
            android:scaleX="1"
            android:scaleY="1"
            android:alpha="1"/>
    </ConstraintSet>

    <ConstraintSet android:id="@+id/curation_animation_start2">
        <Constraint android:id="@+id/redView"
            android:layout_width="120dp"
            android:layout_height="120dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.3"
            app:layout_constraintHorizontal_bias="0.3"
            android:scaleX="1"
            android:scaleY="1"
            android:alpha="1"/>
        <Constraint android:id="@+id/yellowView"
            android:layout_width="120dp"
            android:layout_height="120dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.3"
            app:layout_constraintHorizontal_bias="0.7"
            android:scaleX="1"
            android:scaleY="1"
            android:alpha="1"/>
        <Constraint android:id="@+id/greenView"
            android:layout_width="120dp"
            android:layout_height="120dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.7"
            app:layout_constraintHorizontal_bias="0.3"
            android:scaleX="1"
            android:scaleY="1"
            android:alpha="1"/>
        <Constraint android:id="@+id/blueView"
            android:layout_width="120dp"
            android:layout_height="120dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.7"
            app:layout_constraintHorizontal_bias="0.7"
            android:scaleX="1"
            android:scaleY="1"
            android:alpha="1"/>
        <Constraint android:id="@+id/centerView"
            android:layout_width="180dp"
            android:layout_height="180dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            android:scaleX="0.5"
            android:scaleY="0.5"
            android:alpha="0"/>
    </ConstraintSet>

    <ConstraintSet android:id="@+id/curation_animation_end2">
        <Constraint android:id="@+id/redView"
            android:layout_width="120dp"
            android:layout_height="120dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.2"
            app:layout_constraintHorizontal_bias="0.2"
            android:scaleX="0.8"
            android:scaleY="0.8"
            android:alpha="1"/>
        <Constraint android:id="@+id/yellowView"
            android:layout_width="120dp"
            android:layout_height="120dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.2"
            app:layout_constraintHorizontal_bias="0.8"
            android:scaleX="0.8"
            android:scaleY="0.8"
            android:alpha="1"/>
        <Constraint android:id="@+id/greenView"
            android:layout_width="120dp"
            android:layout_height="120dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.8"
            app:layout_constraintHorizontal_bias="0.2"
            android:scaleX="0.8"
            android:scaleY="0.8"
            android:alpha="1"/>
        <Constraint android:id="@+id/blueView"
            android:layout_width="120dp"
            android:layout_height="120dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintVertical_bias="0.8"
            app:layout_constraintHorizontal_bias="0.8"
            android:scaleX="0.8"
            android:scaleY="0.8"
            android:alpha="1"/>
        <Constraint android:id="@+id/centerView"
            android:layout_width="180dp"
            android:layout_height="180dp"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            android:scaleX="1"
            android:scaleY="1"
            android:alpha="1"/>
    </ConstraintSet>

</MotionScene>

```

### MainActivity.kt
- 먼저 앞서 말했듯이 기존의 AppBar를 쓴 것이 아니라 직접 커스텀을 한 것이기 때문에 그 부분에 대해서 스크롤 처리와 툴바 설정을 함

- 그리고 dp와 px간의 정립을 하기 위해서 함수를 만들어서 사용함

- 한 화면에 모든 애니메이션 처리가 다 들어가 있기 때문에 true/false를 통해서 애니메이션 준비를 체크하고 동작 처리를 통해서 애니메이션을 처리할 타이밍을 구분함

- 그러면서 자연스럽게 3개의 화면이 스크롤하면서 이어질 수 있도록 그 구분을 체크하고 설정하여 애니메이션 처리를 함

```kotlin
package techtown.org.ott

import android.app.Activity
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.AppBarLayout
import techtown.org.ott.databinding.ActivityMainBinding
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    // 뷰바인딩 처리
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    private var isGateringMotionAnimating: Boolean = false
    private var isCurationMotionAnimating: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        makeStatusBarTransparent() // status바까지 투명으로 만듬

        initAppBar() // toolbar, alpha 값을 수정함

        initInsetMargin() // insetmargin에 직접 접근하여서 수정을 함

        initScrollViewListener()

        initMotionLayoutListener()


    }

    private fun initScrollViewListener() {
        binding.scrollView.smoothScrollTo(0,0)

        // 스크롤 뷰 설정할 때 모션 레이아웃 처리를 함
        // 스크롤 한 만큼 value를 조절해줘야함, 더 많이
        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrolledValue = binding.scrollView.scrollY

            if(scrolledValue > 150f.dpToPx(this).toInt()) {
                if(isGateringMotionAnimating.not()) {
                    binding.gatheringDigitalThingsBackgroundMotionLayout.transitionToEnd()
                    binding.gatheringDigitalThingsLayout.transitionToEnd()
                    binding.buttonShownMotionLayout.transitionToEnd()
                }
            } else {
                if(isGateringMotionAnimating.not()) {
                    binding.gatheringDigitalThingsBackgroundMotionLayout.transitionToStart()
                    binding.gatheringDigitalThingsLayout.transitionToStart()
                    binding.buttonShownMotionLayout.transitionToStart()
                }
            }

            // 도형 애니메이션 준비하고 실행시킴, 스크롤뷰보다 더 스크롤이 되면
            if(scrolledValue > binding.scrollView.height) {
                if(isCurationMotionAnimating.not()) {
                    binding.curationAnimationMotionLayout.setTransition(R.id.curation_animation_start1, R.id.curation_animation_end1)
                    binding.curationAnimationMotionLayout.transitionToEnd()
                    isCurationMotionAnimating = true
                }
            }
        }

    }

    private fun initMotionLayoutListener() {
        // 애니메이션 동작 처리 true, false 처리함, 중복 방지를 위해서
        binding.gatheringDigitalThingsLayout.setTransitionListener(object: MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {
                isGateringMotionAnimating = true
            }

            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) = Unit

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                isGateringMotionAnimating = false
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) = Unit

        })

        // 도형 애니메이션 처리
        binding.curationAnimationMotionLayout.setTransitionListener(object: MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) = Unit
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) = Unit

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                // transition이 완료되면 현재 id에서 2번째 transition으로 전환되게 처리함
                when(currentId) {
                    R.id.curation_animation_end1 -> {
                        binding.curationAnimationMotionLayout.setTransition(R.id.curation_animation_start2, R.id.curation_animation_end2)
                        binding.curationAnimationMotionLayout.transitionToEnd()
                    }
                }
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) = Unit

        })
    }

    // 윈도위에 있는 모든 시스템영역의 inset값을 조정하는 함수
    private fun initInsetMargin() = with(binding) {
        // 최상단 coordinator로부터 하단에 있는 것이 다 붙어서 그 WindowInsets값을 커스텀함
        ViewCompat.setOnApplyWindowInsetsListener(coordinator) {v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = insets.systemWindowInsetBottom
            // toolbar로 만든 레이아웃에 대해서 Insets area에서 줄 수 있는 margin을 줄 것임(collapsing도 포함, 단 다 0으로 설정함)
            toolbarContainer.layoutParams = (toolbarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0,insets.systemWindowInsetTop,0,0)
            }
            collapsingToolbarContainer.layoutParams = (collapsingToolbarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0,0,0,0)
            }

            insets.consumeSystemWindowInsets()
        }
    }

    private fun initAppBar() {
        // 스크롤 시 abstractOffset 즉, 특정 스크롤만큼 된다면 alpha 값을 조절함
        // 움직인 값만큼 빼서 alpha를 조정함, 그래서 애니메이션 처리를 좀 더 어색하지 않고 자연스럽게 처리하게함, offset을 직접 체킹하면서 원하는 애니메이션 처리를 함
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val topPadding = 300f.dpToPx(this)
            val realAlphaScrollHeight = appBarLayout.measuredHeight - appBarLayout.totalScrollRange
            val abstractOffset = abs(verticalOffset)

            val realAlphaVerticalOffset = if (abstractOffset - topPadding < 0) 0f else abstractOffset - topPadding

            if(abstractOffset < topPadding) {
                binding.toolbarBackgroundView.alpha = 0f
                return@OnOffsetChangedListener
            }
            // alpha 값을 스크롤 하는 시점에 바꿈
            val percentage = realAlphaVerticalOffset / realAlphaScrollHeight
            binding.toolbarBackgroundView.alpha = 1 - (if (1 - percentage * 2 < 0) 0f else 1 - percentage * 2)
        })
        initActionBar()
    }

    // 기존 앱테마의 툴바가 아닌 다른 툴바를 쓰기 위한 초기화 한 함수
    // 액션바를 활용해서 툴바를 붙일 것임, NoActionBar 설정으로 바탕으로 날림
    private fun initActionBar() = with(binding) {
        toolbar.navigationIcon = null
        toolbar.setContentInsetsAbsolute(0,0)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setHomeButtonEnabled(false)
            it.setDisplayHomeAsUpEnabled(false)
            it.setDisplayShowHomeEnabled(false)
        }
    }

}
fun Float.dpToPx(context: Context): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)

// 시스템 영역의 statusBar를 투명으로 만드는 함수
fun Activity.makeStatusBarTransparent() {
    window.apply {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = Color.TRANSPARENT
    }
}
```