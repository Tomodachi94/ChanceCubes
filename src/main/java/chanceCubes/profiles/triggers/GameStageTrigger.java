package chanceCubes.profiles.triggers;

import chanceCubes.profiles.IProfile;

public class GameStageTrigger implements ITrigger<String>
{
	private IProfile prof;
	private String stageName;

	public GameStageTrigger(IProfile prof, String stageName)
	{
		this.prof = prof;
		this.stageName = stageName;
	}

	@Override
	public void onTrigger(String playerUUID, String[] args)
	{
		if(args.length == 2)
		{
			if(args[0].equals(stageName))
			{
				if(args[1].equals("A"))
					prof.setTriggerState(this, playerUUID, true);
				else
					prof.setTriggerState(this, playerUUID, false);
			}
		}
	}

	@Override
	public String getTriggerDesc()
	{
		return "Trigger on game stage change";
	}
}
