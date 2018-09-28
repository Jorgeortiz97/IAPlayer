package com.jorgeortizesc.iaplayer.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.jorgeortizesc.iaplayer.Launcher;
import com.jorgeortizesc.iaplayer.domain.Advice;
import com.jorgeortizesc.iaplayer.domain.IAElement;
import com.jorgeortizesc.iaplayer.domain.Input;
import com.jorgeortizesc.iaplayer.domain.Question;
import com.jorgeortizesc.iaplayer.util.ErrorDialog;
import com.jorgeortizesc.iaplayer.util.InfoDialog;
import com.jorgeortizesc.iaplayer.util.TimeUtil;
import com.jorgeortizesc.iaplayer.util.XMLReader;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController {

	@FXML
	private MediaView mediaView;
	@FXML
	private ImageView backImage;
	@FXML
	private ImageView playImage;
	@FXML
	private ImageView stopImage;
	@FXML
	private ImageView forwardImage;


	@FXML
	private Slider timeSlider;
	@FXML
	private Slider volSlider;
	@FXML
	private Label totalTime, currentTime;


	@FXML
	private Label title;
	@FXML
	private ImageView reviewBtn;
	@FXML
	private ImageView goBtn;
	@FXML
	private VBox pane;
	@FXML
	private VBox bigPane;

	private MediaPlayer mp;


	private Image playIcon = new Image(Launcher.class.getResource("view/img/play.png").toExternalForm());
	private Image pauseIcon = new Image(Launcher.class.getResource("view/img/pause.png").toExternalForm());
	private Image continueIcon = new Image(Launcher.class.getResource("view/img/continue.png").toExternalForm());
	private Image reviewIcon = new Image(Launcher.class.getResource("view/img/review.png").toExternalForm());
	private Image checkIcon = new Image(Launcher.class.getResource("view/img/check.png").toExternalForm());
	private Image backIcon = new Image(Launcher.class.getResource("view/img/back.png").toExternalForm());
	private Image stopIcon = new Image(Launcher.class.getResource("view/img/stop.png").toExternalForm());
	private Image forwardIcon = new Image(Launcher.class.getResource("view/img/forward.png").toExternalForm());

	private Stage stage;
	private FileChooser fileChooser;
	private File lastFile = null;
	private ChangeListener<Number> timeSliderListener;

	private AnimationTimer timer;

	private List<IAElement> elements = null;
	private boolean answering;


	public MainController(Stage stage) {
		this.stage = stage;

		// File Chooser
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open a video file");
        FileChooser.ExtensionFilter allExt = new FileChooser.ExtensionFilter("All supported video files", "*.flv", "*.mp4");
        FileChooser.ExtensionFilter flvExt = new FileChooser.ExtensionFilter("FLV files (*.flv)", "*.flv");
        FileChooser.ExtensionFilter mp4Ext = new FileChooser.ExtensionFilter("MPEG-4 files (*.mp4)", "*.mp4");
        fileChooser.getExtensionFilters().addAll(allExt, flvExt, mp4Ext);


        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                checkQuestions();
            }
        };
	}

	public void init() {
        // Listener for resizing frame
		mediaView.getParent().layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
	        @Override
	        public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
	            mediaView.setFitHeight(newValue.getHeight());
	            mediaView.setFitWidth(newValue.getWidth());
	        }
	    });


		// Fullscreen
		mediaView.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		        if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2)
		            handleFullscreen();
		    }
		});

		reviewBtn.setImage(reviewIcon);
		backImage.setImage(backIcon);
		playImage.setImage(playIcon);
		stopImage.setImage(stopIcon);
		forwardImage.setImage(forwardIcon);
	}




	/* Handle file events */
    public void handleOpen() {

        if (lastFile != null && lastFile.getParentFile().exists())
        	fileChooser.setInitialDirectory(lastFile.getParentFile());
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
        	lastFile = file;
        	if (mp != null) mp.stop();
        	String videoFile = file.getAbsolutePath();
        	boolean error = false;

        	try {
        		elements = XMLReader.read(videoFile);
			} catch (IllegalArgumentException e) {
				ErrorDialog.show("Error with XML file", e.getMessage());
				error = true;
			} catch (ParserConfigurationException | SAXException e) {
				ErrorDialog.show("Error with XML file", "An error occurred when parsing the XML file.");
				error = true;
			} catch (IOException e) {
				ErrorDialog.show("Error with XML file", e.getMessage());
				error = true;
			}

        	if (error) return;


        	loadVideo(videoFile);
        }

    }
	public void handleExit() {
		System.exit(0);
	}

	/* Handle media control events */

	public void handleBackward() {
		if (mp == null) return;
		mp.seek(mp.getCurrentTime().subtract(Duration.seconds(5)));
	}


	private void pauseVideo() {
		playImage.setImage(playIcon);
		mp.pause();
	}

	private void playVideo() {
		playImage.setImage(pauseIcon);
		mp.play();
		timer.start();
	}

	public void handlePlay() {
		if (mp == null || answering) return;
		if (mp.getStatus() == Status.PLAYING)
			pauseVideo();
		else
			playVideo();
	}

	public void handleStop() {
		if (mp == null) return;
		for (IAElement e: elements)
			e.setActive(true);

		returnToVideo();
		mp.stop();
		timer.stop();
	}

	public void handleForward() {
		if (mp == null || answering) return;
		mp.seek(mp.getCurrentTime().add(Duration.seconds(5)));
	}



	/* Handle methods for controlling video rate */

	public void handleSlow() {
		mp.setRate(0.75);
	}
	public void handleNormal() {
		mp.setRate(1);
	}
	public void handleFast() {
		mp.setRate(1.5);
	}
	public void handleVeryFast() {
		mp.setRate(2);
	}


	public void handleFullscreen() {
		stage.setFullScreen(!stage.isFullScreen());
	}

	public void handleAbout() {


		String content = "IAPlayer is a free and open source media player developed by Jorge Ortiz."
				+ "It allows you to play interactive videos according to an XML format specification."
				+ "To find more information, please visit 'http://jorgeortizesc.com/es/IAPlayer.html'.";

		InfoDialog.show("About", content);
	}

	public void loadVideo(String file) {
		 mp = new MediaPlayer(new Media(new File(file).toURI().toString()));

		mp.setOnReady(() -> {

			timeSlider.setMax(mp.getTotalDuration().toSeconds());
			totalTime.setText(TimeUtil.durationToString(mp.getTotalDuration()));
			if (timeSliderListener != null)
				timeSlider.valueProperty().removeListener(timeSliderListener);

			timeSliderListener = (p, o, value) -> {
		        if (timeSlider.isPressed() && !answering) {
		            mp.seek(Duration.seconds(value.doubleValue()));
		        }
		    };

		    timeSlider.valueProperty().addListener(timeSliderListener);

		    mp.currentTimeProperty().addListener((p, o, value) -> {
		    	currentTime.setText(TimeUtil.durationToString(value));
		    	timeSlider.setValue(value.toSeconds());
		    });
		});

        // Video
        mediaView.setMediaPlayer(mp);

		mp.setOnEndOfMedia(() -> {
			mp.seek(Duration.ZERO);
			handleStop();
		});

		mp.setOnStopped(() -> {
			playImage.setImage(playIcon);
		});

		playImage.setImage(pauseIcon);

		mp.volumeProperty().bind(volSlider.valueProperty());

		mp.play();

		// Questions:
		answering = false;
		timer.start();

	}


	/* Variable IAElement */
	private IAElement element;

	/* Variables for a question */
	private Question question;
	private RadioButton[] options;


	/* Variables for an input */
	private Input input;
	private TextArea text;

	/* Variables for both of them */
	private boolean answered; // True -> Question was answered; false, otherwise




	private void updateButton() {
		if (answered)
			goBtn.setImage(continueIcon);
		else
			goBtn.setImage(checkIcon);
	}

	/***
	 * Return to the video
	 */
	private void returnToVideo() {
		bigPane.setVisible(false);
		answering = false;
		playVideo();
	}

	public void handleReview() {
		element.setActive(true);
		mp.seek(element.getExplanation());
		returnToVideo();
	}

	public void handleGo() {

		if (answered)
			returnToVideo();
		else {
			if (element instanceof Question) {
				int chosen = -1;
				options[question.getRight()].setStyle("-fx-text-fill: green");
				for (int i = 0; i < options.length; i++)
					if (options[i].isSelected()) {
						chosen = i;
						break;
					}
				if (chosen != question.getRight() && chosen != -1)
					options[chosen].setStyle("-fx-text-fill: red");
			} else if (element instanceof Input) {
				String userAns = text.getText();
				String[] rightAns = input.getAnswers();
				int i = 0;
				while (i < rightAns.length && !userAns.equals(rightAns[i])) i++;
				if (i == rightAns.length) {
					text.setText(rightAns[0]);
					text.setStyle("-fx-text-fill: red");
				} else
					text.setStyle("-fx-text-fill: green");
			}
		}

		answered = true;
		updateButton();
	}

	private void showElement(IAElement e) {

		pauseVideo();
		answering = true;

		bigPane.setVisible(true);
		pane.getChildren().clear();

		title.setText(e.getTitle());

		if (e.getExplanation() == null) {
			reviewBtn.setDisable(true);
			reviewBtn.setStyle("-fx-opacity: 0.5;");
		} else {
			reviewBtn.setDisable(false);
			reviewBtn.setStyle("-fx-opacity: 1;");
		}

		if (e instanceof Input) {
			answered = false;

			input = (Input) e;
			// Set the textarea
			text = new TextArea();
			text.setWrapText(true);
			pane.getChildren().add(text);

		} else if (e instanceof Question) {
			answered = false;

			question = (Question) e;
	        ToggleGroup tg = new ToggleGroup();
			String[] answers = question.getAnswers();
			options = new RadioButton[answers.length];
			for (int i = 0; i < answers.length; i++) {
				options[i] = new RadioButton(answers[i]);
				options[i].setToggleGroup(tg);
			}

			pane.getChildren().addAll(options);
		} else if (e instanceof Advice) {
			answered = true;

			Advice a = (Advice) e;
			Label contentLbl = new Label(a.getContent());
			contentLbl.setWrapText(true);
			pane.getChildren().add(contentLbl);
		}

		updateButton();
	}

	private void checkQuestions() {


		for (IAElement e: elements)
			if (e.isActive() && mp.getCurrentTime().greaterThan(e.getTime())) {
				element = e;
				mp.seek(e.getTime());
				showElement(e);
				e.setActive(false);
				return;
			}
	}

}
