package be.ac.ua.comp.scarletnebula.wizard;

public interface WizardListener
{
	public void onFinish(DataRecorder recorder);

	public void onCancel(DataRecorder recorder);
}
