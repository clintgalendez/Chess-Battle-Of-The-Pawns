package com.gui_components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Timer;

public class FadingComponent extends JLayeredPane {
    private double alpha = 1;
    private boolean fadingIn = true;
    private DoubleAnimatable animatable;
    private Duration duration = Duration.ofSeconds(1);
    private List<FaderListener> listeners = new ArrayList<>(1);

    public double getAlpha() {
        return alpha;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.SrcOver.derive((float)getAlpha()));
        g2d.setColor(getBackground());
        super.paint(g2d);
        g2d.dispose();
    }

    protected void fadeTo(double to) {
        double currentAlpha = getAlpha();
        if (animatable != null) {
            animatable.stop();
            animatable = null;
        }

        if (currentAlpha == to) {
            fadeDidComplete();
            return;
        }

        DoubleRange animationRange = new DoubleRange(currentAlpha, to);
        double maxFrom = to == 1 ? 1 : 0;
        double maxTo = to == 1 ? 0 : 1;
        DoubleRange maxRange = new DoubleRange(maxFrom, maxTo);

        animatable = new DoubleAnimatable(animationRange, maxRange, duration, new AnimatableListener<Double>() {
            @Override
            public void animationChanged(Animatable<Double> animatable) {
                alpha = animatable.getValue();
                repaint();
            }
        }, new AnimatableLifeCycleListenerAdapter<Double>() {
            @Override
            public void animationCompleted(Animatable<Double> animatable) {
                fadeDidComplete();
            }
        });

        Animator.INSTANCE.add(animatable);
    }

    public void fadeIn() {
        fadingIn = true;
        fadeTo(1);
    }

    public void fadeOut() {
        fadingIn = false;
        fadeTo(0);
    }

    protected void fadeDidComplete() {            
        for (FaderListener listener : listeners) {
            listener.fadeDidComplete(this);
        }
    }

    public interface FaderListener {
        public void fadeDidComplete(FadingComponent fadingComponent);
    }

    public class DoubleAnimatable extends AbstractAnimatable<Double> {

        public DoubleAnimatable(DoubleRange animationRange, DoubleRange maxRange, Duration duration, AnimatableListener<Double> listener, AnimatableLifeCycleListener<Double> lifeCycleListener) {
            super(animationRange, duration, listener, lifeCycleListener);
    
            double maxDistance = maxRange.getDistance();
            double aniDistance = animationRange.getDistance();
    
            double progress = Math.min(100, Math.max(0, Math.abs(aniDistance / maxDistance)));
            Duration remainingDuration = Duration.ofMillis((long) (duration.toMillis() * progress));
            setDuration(remainingDuration);
        }
    }
    
    public interface AnimatableListener<T> {
        public void animationChanged(Animatable<T> animatable);
    }
    
    public interface AnimatableLifeCycleListener<T> {
        public void animationStopped(Animatable<T> animatable);
        public void animationCompleted(Animatable<T> animatable);
        public void animationStarted(Animatable<T> animatable);
        public void animationPaused(Animatable<T> animatable);        
    }
    
    public class AnimatableLifeCycleListenerAdapter<T> implements AnimatableLifeCycleListener<T> {
    
        @Override
        public void animationStopped(Animatable<T> animatable) {
        }
    
        @Override
        public void animationCompleted(Animatable<T> animatable) {
        }
    
        @Override
        public void animationStarted(Animatable<T> animatable) {
        }
    
        @Override
        public void animationPaused(Animatable<T> animatable) {
        }
    
    }
    
    public abstract class AbstractAnimatable<T> implements Animatable<T> {
    
        private Range<T> range;
        private LocalDateTime startTime;
        private Duration duration = Duration.ofSeconds(5);
        private T value;
        private AnimatableListener<T> animatableListener;
        private AnimatableLifeCycleListener<T> lifeCycleListener;
        private double rawOffset;
    
        public AbstractAnimatable(Range<T> range, Duration duration, AnimatableListener<T> listener) {
            this.range = range;
            this.value = range.getFrom();
            this.animatableListener = listener;
        }
    
        public AbstractAnimatable(Range<T> range, Duration duration, AnimatableListener<T> listener, AnimatableLifeCycleListener<T> lifeCycleListener) {
            this(range, duration, listener);
            this.lifeCycleListener = lifeCycleListener;
        }
    
        public Duration getDuration() {
            return duration;
        }
    
        public Range<T> getRange() {
            return range;
        }
    
        public void setRange(Range<T> range) {
            this.range = range;
        }
    
        @Override
        public T getValue() {
            return value;
        }
    
        protected void setDuration(Duration duration) {
            this.duration = duration;
        }
    
        public double getCurrentProgress(double rawProgress) {
            double progress = Math.min(1.0, Math.max(0.0, getRawProgress()));
            return Math.min(1.0, Math.max(0.0, progress));
        }
    
        public double getRawProgress() {
            if (startTime == null) {
                return 0.0;
            }
            Duration duration = getDuration();
            Duration runningTime = Duration.between(startTime, LocalDateTime.now());
            double progress = rawOffset + (runningTime.toMillis() / (double) duration.toMillis());
    
            return Math.min(1.0, Math.max(0.0, progress));
        }
    
        @Override
        public void tick() {
            if (startTime == null) {
                startTime = LocalDateTime.now();
                fireAnimationStarted();
            }
            double rawProgress = getRawProgress();
            double progress = getCurrentProgress(rawProgress);
            if (rawProgress >= 1.0) {
                progress = 1.0;
            }
            value = getRange().valueAt(progress);
            fireAnimationChanged();
            if (rawProgress >= 1.0) {
                fireAnimationCompleted();
            }
        }
    
        @Override
        public void start() {
            if (startTime != null) {
                return;
            }
            Animator.INSTANCE.add(this);
        }
    
        @Override
        public void stop() {
            stopWithNotification(true);
        }
    
        @Override
        public void pause() {
            rawOffset += getRawProgress();
            stopWithNotification(false);
    
            double remainingProgress = 1.0 - rawOffset;
            Duration remainingTime = getDuration().minusMillis((long) remainingProgress);
            setDuration(remainingTime);
    
            lifeCycleListener.animationStopped(this);
        }
    
        protected void fireAnimationChanged() {
            if (animatableListener == null) {
                return;
            }
            animatableListener.animationChanged(this);
        }
    
        protected void fireAnimationCompleted() {
            stopWithNotification(false);
            if (lifeCycleListener == null) {
                return;
            }
            lifeCycleListener.animationCompleted(this);
        }
    
        protected void fireAnimationStarted() {
            if (lifeCycleListener == null) {
                return;
            }
            lifeCycleListener.animationStarted(this);
        }
    
        protected void fireAnimationPaused() {
            if (lifeCycleListener == null) {
                return;
            }
            lifeCycleListener.animationPaused(this);
        }
    
        protected void stopWithNotification(boolean notify) {
            Animator.INSTANCE.remove(this);
            startTime = null;
            if (notify) {
                if (lifeCycleListener == null) {
                    return;
                }
                lifeCycleListener.animationStopped(this);
            }
        }
    
    }
    
    public interface Animatable<T> {
    
        public Range<T> getRange();
    
        public T getValue();
    
        public void tick();
    
        public Duration getDuration();
    
        public void start();
    
        public void stop();
    
        public void pause();
    }
    
    public abstract class Range<T> {
    
        private T from;
        private T to;
    
        public Range(T from, T to) {
            this.from = from;
            this.to = to;
        }
    
        public T getFrom() {
            return from;
        }
    
        public T getTo() {
            return to;
        }
    
        @Override
        public String toString() {
            return "From " + getFrom() + " to " + getTo();
        }
    
        public abstract T valueAt(double progress);
    
    }
    
    public class DoubleRange extends Range<Double> {
    
        public DoubleRange(Double from, Double to) {
            super(from, to);
        }
    
        public Double getDistance() {
            return getTo() - getFrom();
        }
    
        @Override
        public Double valueAt(double progress) {
            double distance = getDistance();
            double value = distance * progress;
            value += getFrom();
            return value;
        }
    }
    
    public enum Animator {
        INSTANCE;
        private Timer timer;
        private List<Animatable> properies;
    
        private Animator() {
            properies = new ArrayList<>(5);
            timer = new Timer(5, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    List<Animatable> copy = new ArrayList<>(properies);
                    Iterator<Animatable> it = copy.iterator();
                    while (it.hasNext()) {
                        Animatable ap = it.next();
                        ap.tick();
                    }
                    if (properies.isEmpty()) {
                        timer.stop();
                    }
                }
            });
        }
    
        public void add(Animatable ap) {
            properies.add(ap);
            timer.start();
        }
    
        protected void removeAll(List<Animatable> completed) {
            properies.removeAll(completed);
        }
    
        public void remove(Animatable ap) {
            properies.remove(ap);
            if (properies.isEmpty()) {
                timer.stop();
            }
        }
    }
}